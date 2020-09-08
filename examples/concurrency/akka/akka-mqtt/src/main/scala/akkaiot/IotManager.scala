package akkaiot

import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration._
import akka.actor.{ Props, ActorRef, Actor, ActorLogging, Terminated }
import akka.pattern._
import akka.util.Timeout
import akka.cluster.client.ClusterClient.SendToAll

import com.sandinh.paho.akka._
import com.sandinh.paho.akka.MqttPubSub._

object IotManager {
  def props(clusterClient: ActorRef, numOfDevices: Int, mqttPubSub: ActorRef): Props = Props(
    new IotManager(clusterClient, numOfDevices, mqttPubSub)
  )

  case class Ok(work: Work)
  case class NotOk(work: Work)
}

class IotManager(clusterClient: ActorRef, numOfDevices: Int, mqttPubSub: ActorRef) extends Actor with ActorLogging {
  import IotManager._
  import context.dispatcher

  private var idToActorMap = Map.empty[String, ActorRef]
  private var actorToIdMap = Map.empty[ActorRef, String]

  val deviceTypes = List("thermostat", "lamp", "security-alarm")
  def random = ThreadLocalRandom.current

  mqttPubSub ! Subscribe(MqttConfig.topic, self)

  override def preStart(): Unit = {
    log.info("IoT Manager -> Creating devices ...")

    (1 to numOfDevices).foreach { n =>
      val deviceType = deviceTypes(random.nextInt(0, deviceTypes.size))
      val deviceId = (1000 + n).toString
      val deviceActor = context.actorOf(Device.props(deviceType, deviceId, mqttPubSub), s"$deviceType-$deviceId")
      context.watch(deviceActor)
      actorToIdMap += deviceActor -> deviceId
      idToActorMap += deviceId -> deviceActor
    }

    log.info("IoT Manager -> Created {} devices!", numOfDevices)
  }

  override def postStop(): Unit = log.info("IoT Manager -> Stopped")

  override def receive: Receive = {
    case SubscribeAck(Subscribe(MqttConfig.topic, `self`, _)) => {
      log.info("IoT Manager -> MQTT subscription to {} acknowledged", MqttConfig.topic)
      context.become(ready)
    }

    case x =>
      log.info("IoT Manager -> ALERT: Problem receiving message ... {}", x)
  }

  def ready: Receive = {
    case msg: Message => {
      val work = MqttConfig.readFromByteArray[Work](msg.payload)
      log.info("IoT Agent -> Received MQTT message: {}-{} | State {} | Setting {}", work.deviceType, work.deviceId, work.currState, work.currSetting)

      log.info("IoT Manager -> Sending work to cluster master")
      implicit val timeout = Timeout(5.seconds)
      (clusterClient ? SendToAll("/user/master/singleton", work)) map {
        case Master.Ack(_) => Ok(work)
      } recover { case _ => NotOk(work)
      } pipeTo {
        idToActorMap.getOrElse(work.deviceId, `self`)
      }
    }

    case result @ WorkResult(workId, deviceType, deviceId, nextState, nextSetting) =>
      idToActorMap.get(deviceId) match {
        case Some(deviceActor) =>
          deviceActor forward result
          log.info("IoT Manager -> Work result forwarded to {}-{} ", deviceType, deviceId)
        case None =>
          log.info("IoT Manager -> ALERT: {}-{} NOT in registry!", deviceType, deviceId)
      }

    case Terminated(deviceActor) =>
      val deviceId = actorToIdMap(deviceActor)
      log.info("IoT Manager -> ALERT: Device actor terminated! Device Id {} will be removed.", deviceId)
      actorToIdMap -= deviceActor
      idToActorMap -= deviceId

    case Ok(work) =>
      log.info("IoT Manager -> ALERT: Receive ack from Master but Device Id of {}-{} NOT in registry!", work.deviceType, work.deviceId)

    case NotOk(work) =>
      log.info("IoT Manager -> ALERT: Did not receive ack from Master and Device Id of {}-{} NOT in registry!", work.deviceType, work.deviceId)

    case x =>
      log.info("IoT Manager -> ALERT: Problem with received message ... {}", x)
  }
}
