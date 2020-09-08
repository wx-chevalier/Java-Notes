package akkaiot

import scala.concurrent.duration.Deadline
import scala.concurrent.duration.FiniteDuration
import akka.actor.{ Props, ActorRef, Actor, ActorLogging }
import akka.persistence.PersistentActor
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.cluster.Cluster
import akka.cluster.client.ClusterClientReceptionist

object Master {

  val ResultsTopic = "results"

  def props(workTimeout: FiniteDuration): Props =
    Props(classOf[Master], workTimeout)

  case class Ack(workId: String)

  private sealed trait WorkerStatus
  private case object Idle extends WorkerStatus
  private case class Busy(workId: String, deadline: Deadline) extends WorkerStatus
  private case class WorkerState(ref: ActorRef, status: WorkerStatus)

  private case object CleanupTick

}

class Master(workTimeout: FiniteDuration) extends PersistentActor with ActorLogging {
  import Master._
  import WorkQueue._

  val mediator = DistributedPubSub(context.system).mediator
  ClusterClientReceptionist(context.system).registerService(self)

  // persistenceId must include cluster role to support multiple masters
  override def persistenceId: String = Cluster(context.system).selfRoles.find(_.startsWith("backend-")) match {
    case Some(role) => role + "-master"
    case None       => "master"
  }

  // workers state is not event sourced
  private var workers = Map[String, WorkerState]()

  // workQueue is event sourced
  private var workQueue = WorkQueue.empty

  import context.dispatcher
  val cleanupTask = context.system.scheduler.schedule(workTimeout / 2, workTimeout / 2, self, CleanupTick)

  override def postStop(): Unit = cleanupTask.cancel()

  override def receiveRecover: Receive = {
    case event: WorkDomainEvent =>
      // only update current state by applying the event, no side effects
      workQueue = workQueue.updated(event)
      log.info("Cluster Master -> Replayed event: {}", event.getClass.getSimpleName)
  }

  override def receiveCommand: Receive = {
    case MasterWorkerProtocol.RegisterWorker(workerId) =>
      if (workers.contains(workerId)) {
        workers += (workerId -> workers(workerId).copy(ref = sender()))
      } else {
        log.info("Cluster Master -> Worker registered: Worker Id {}", workerId)
        workers += (workerId -> WorkerState(sender(), status = Idle))
        if (workQueue.hasWork)
          sender() ! MasterWorkerProtocol.WorkIsReady
      }

    case MasterWorkerProtocol.WorkerRequestsWork(workerId) =>
      if (workQueue.hasWork) {
        workers.get(workerId) match {
          case Some(s @ WorkerState(_, Idle)) =>
            val work = workQueue.nextWork
            persist(WorkStarted(work.workId)) { event =>
              workQueue = workQueue.updated(event)
              log.info("Cluster Master -> Delegating work for {}-{} to Worker {} | Work Id {}", work.deviceType, work.deviceId, workerId, work.workId)
              workers += (workerId -> s.copy(status = Busy(work.workId, Deadline.now + workTimeout)))
              sender() ! work
            }
          case _ =>
        }
      }

    case MasterWorkerProtocol.WorkIsDone(workerId, workId, result) =>
      // idempotent
      if (workQueue.isDone(workId)) {
        // previous Ack was lost, confirm again that this is done
        sender() ! MasterWorkerProtocol.Ack(workId)
      } else if (!workQueue.isInProgress(workId)) {
        log.info("Cluster Master -> ALERT: Work NOT IN PROGRESS! Work Id {} | Worker {}", workId, workerId)
      } else {
        log.info("Cluster Master -> Acknowledged work done: Work Id {} | Worker {}", workId, workerId)
        changeWorkerToIdle(workerId, workId)
        persist(WorkCompleted(workId, result)) { event =>
          workQueue = workQueue.updated(event)
          mediator ! DistributedPubSubMediator.Publish(ResultsTopic, result)
          // Ack back to original sender
          sender ! MasterWorkerProtocol.Ack(workId)
        }
      }

    case MasterWorkerProtocol.WorkFailed(workerId, workId) =>
      if (workQueue.isInProgress(workId)) {
        log.info("Cluster Master -> ALERT: Work FAILED! Work Id {} | Worker Id {}", workId, workerId)
        changeWorkerToIdle(workerId, workId)
        persist(WorkerFailed(workId)) { event =>
          workQueue = workQueue.updated(event)
          notifyWorkers()
        }
      }

    case work: Work =>
      // idempotent
      if (workQueue.isAccepted(work.workId)) {
        sender() ! Master.Ack(work.workId)
      } else {
        log.info("Cluster Master -> Accepted work for {}-{} : Work Id {}", work.deviceType, work.deviceId, work.workId)
        persist(WorkAccepted(work)) { event =>
          // Ack back to original sender
          sender() ! Master.Ack(work.workId)
          workQueue = workQueue.updated(event)
          notifyWorkers()
        }
      }

    case CleanupTick =>
      for ((workerId, s @ WorkerState(_, Busy(workId, timeout))) ← workers) {
        if (timeout.isOverdue) {
          log.info("Cluster Master -> ALERT: TIMED OUT! Work Id {}", workId)
          workers -= workerId
          persist(WorkerTimedOut(workId)) { event =>
            workQueue = workQueue.updated(event)
            notifyWorkers()
          }
        }
      }
  }

  def notifyWorkers(): Unit =
    if (workQueue.hasWork) {
      // could pick a few random instead of all
      workers.foreach {
        case (_, WorkerState(ref, Idle)) => ref ! MasterWorkerProtocol.WorkIsReady
        case _                           => // busy
      }
    }

  def changeWorkerToIdle(workerId: String, workId: String): Unit =
    workers.get(workerId) match {
      case Some(s @ WorkerState(_, Busy(`workId`, _))) =>
        workers += (workerId -> s.copy(status = Idle))
      case _ =>
      // Might happen after standby recovery (worker state is not persisted)
    }

}