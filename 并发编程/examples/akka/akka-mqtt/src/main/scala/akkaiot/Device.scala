package akkaiot

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration._
import akka.actor.{ Props, ActorRef, Actor, ActorLogging }

import com.sandinh.paho.akka._
import com.sandinh.paho.akka.MqttPubSub._

object Device {
  def props(deviceType: String, deviceId: String, mqttPubSub: ActorRef): Props = Props(new Device(deviceType, deviceId, mqttPubSub))

  case object Tick
}

class Device(deviceType: String, deviceId: String, mqttPubSub: ActorRef) extends Actor with ActorLogging {

  import Device._
  import context.dispatcher

  // deviceTypes = List("thermostat", "lamp", "security-alarm")
  private var opState: Int = 0  // 0|1|2 (OFF|HEAT|COOL) for thermostat, 0|1 (OFF|ON) for lamp|security-alarm
  private var setting: Int = 0  // 60-75 for themostat, 1-3 for lamp, 1-5 for security-alarm

  def scheduler = context.system.scheduler
  def random = ThreadLocalRandom.current
  def nextWorkId(): String = UUID.randomUUID().toString

  override def preStart(): Unit = {
    opState = deviceType match {
      case "thermostat" => random.nextInt(0, 2+1)
      case "lamp" => random.nextInt(0, 1+1)
      case "security-alarm" => random.nextInt(0, 1+1)
      case _ => 0
    }

    setting = deviceType match {
      case "thermostat" => random.nextInt(60, 75+1)
      case "lamp" => random.nextInt(1, 3+1)
      case "security-alarm" => random.nextInt(1, 5+1)
      case _ => 0
    }

    scheduler.scheduleOnce(5.seconds, self, Tick)
    log.info("Device -> {}-{} started", deviceType, deviceId)
  }

  override def postRestart(reason: Throwable): Unit = ()

  override def postStop(): Unit = log.info("Device -> {}-{} stopped.", deviceType, deviceId)

  def receive = {
    case Tick => {
      val workId = nextWorkId()
      val work = Work(workId, deviceType, deviceId, opState, setting)
      log.info("Device -> {}-{} with state {} created work (Id: {}) ", deviceType, deviceId, opState, workId)

      val payload = MqttConfig.writeToByteArray(work)
      log.info("Device -> Publishing MQTT Topic {}: Device {}-{}", MqttConfig.topic, deviceType, deviceId)

      mqttPubSub ! new Publish(MqttConfig.topic, payload)

      context.become(waitAccepted(work, payload), discardOld = false)
    }

    case WorkResult(workId, deviceType, deviceId, nextState, nextSetting) => {
      log.info("Device -> {}-{} received work result with work Id {}.", deviceType, deviceId, workId)

      opState = nextState
      setting = nextSetting

      log.info("Device -> Updated {}-{} with state {} and setting {}.", deviceType, deviceId, opState, setting)
    }
  }

  def waitAccepted(work: Work, payload: Array[Byte]): Receive = {
    case IotManager.Ok(_) =>
      log.info("Device -> Work for {}-{} accepted | Work Id {}", work.deviceType, work.deviceId, work.workId)
      context.unbecome()
      scheduler.scheduleOnce(random.nextInt(3, 10).seconds, self, Tick)

    case IotManager.NotOk(_) =>
      log.info("Device -> ALERT: Work from {}-{} NOT ACCEPTED | Work Id {} | Retrying ... ", work.deviceType, work.deviceId, work.workId)
      scheduler.scheduleOnce(3.seconds, mqttPubSub, new Publish(MqttConfig.topic, payload))
  }
}
