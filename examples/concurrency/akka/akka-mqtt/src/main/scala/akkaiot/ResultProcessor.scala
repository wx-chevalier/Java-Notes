package akkaiot

import akka.actor.{ Props, ActorRef, Actor, ActorLogging, ActorPath }
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator

object ResultProcessor {
  def props(iotPath: ActorPath): Props = Props(new ResultProcessor(iotPath))
}

class ResultProcessor(iotPath: ActorPath) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! DistributedPubSubMediator.Subscribe(Master.ResultsTopic, self)

  def receive = {
    case _: DistributedPubSubMediator.SubscribeAck =>
    case result: WorkResult =>
      log.info("Result Processor -> Got work result: {}-{} | State {} | Setting {}", result.deviceType, result.deviceId, result.nextState, result.nextSetting)

      context.actorSelection(iotPath) ! result
      log.info("Result Processor -> Sent work result for {}-{} to IoT Manager", result.deviceType, result.deviceId)
  }
}