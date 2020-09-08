package akkaiot

import java.util.UUID
import scala.concurrent.duration._
import akka.actor.{ Props, ActorRef, Actor, ActorLogging, ReceiveTimeout, Terminated }
import akka.cluster.client.ClusterClient.SendToAll
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.Stop
import akka.actor.SupervisorStrategy.Restart
import akka.actor.ActorInitializationException
import akka.actor.DeathPactException

object Worker {

  def props(clusterClient: ActorRef, workProcessorProps: Props, registerInterval: FiniteDuration = 10.seconds): Props =
    Props(classOf[Worker], clusterClient, workProcessorProps, registerInterval)

  case class WorkProcessed(result: WorkResult)
}

class Worker(clusterClient: ActorRef, workProcessorProps: Props, registerInterval: FiniteDuration)
  extends Actor with ActorLogging {
  import Worker._
  import MasterWorkerProtocol._

  val workerId = UUID.randomUUID().toString

  import context.dispatcher
  val registerTask = context.system.scheduler.schedule(0.seconds, registerInterval, clusterClient,
    SendToAll("/user/master/singleton", RegisterWorker(workerId)))

  val workProcessor = context.watch(context.actorOf(workProcessorProps, "work-processor"))

  var currentWorkId: Option[String] = None
  def workId: String = currentWorkId match {
    case Some(workId) => workId
    case None => throw new IllegalStateException("Not working")
  }

  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException => Stop
    case _: Exception =>
      currentWorkId foreach { workId => sendToMaster(WorkFailed(workerId, workId)) }
      context.become(idle)
      Restart
  }

  override def postStop(): Unit = registerTask.cancel()

  def receive = idle

  def idle: Receive = {
    case WorkIsReady =>
      sendToMaster(WorkerRequestsWork(workerId))

    case work @ Work(workId, deviceType, deviceId, state, setting) =>
      log.info("Worker -> Received work request from {}-{} | State {} | Setting {}", deviceType, deviceId, state, setting)
      currentWorkId = Some(workId)
      workProcessor ! work
      context.become(working)
  }

  def working: Receive = {
    case WorkProcessed(result: WorkResult) =>
      log.info("Worker -> Processed work: {}-{} | Work Id {}", result.deviceType, result.deviceId, workId)
      sendToMaster(WorkIsDone(workerId, workId, result))
      context.setReceiveTimeout(5.seconds)
      context.become(waitForWorkIsDoneAck(result))

    case work: Work =>
      log.info("Worker -> ALERT: Worker Id {} NOT AVAILABLE for Work Id {}", workerId, work.workId)
  }

  def waitForWorkIsDoneAck(result: WorkResult): Receive = {
    case Ack(id) if id == workId =>
      sendToMaster(WorkerRequestsWork(workerId))
      context.setReceiveTimeout(Duration.Undefined)
      context.become(idle)
    case ReceiveTimeout =>
      log.info("Worker -> ALERT: NO ACK from cluster master, retrying ... ")
      sendToMaster(WorkIsDone(workerId, workId, result))
  }

  override def unhandled(message: Any): Unit = message match {
    case Terminated(`workProcessor`) => context.stop(self)
    case WorkIsReady =>
    case _ => super.unhandled(message)
  }

  def sendToMaster(msg: Any): Unit = {
    clusterClient ! SendToAll("/user/master/singleton", msg)
  }
}