package akkaiot

import akka.actor.{ Props, Actor, ActorLogging }
import java.util.concurrent.ThreadLocalRandom

object WorkProcessor {
  def props(): Props = Props(new WorkProcessor)

  case class DeviceStateSetting(deviceType: String, state: Int, setting: Int)
}

class WorkProcessor extends Actor with ActorLogging {
  import WorkProcessor._
  def random = ThreadLocalRandom.current

  def receive = {
    case work @ Work(workId, deviceType, deviceId, state, setting) => {
      val newStateSetting: DeviceStateSetting = deviceType match {
        case "thermostat" =>
          nextDeviceStateSetting(work, Map(0->"OFF", 1->"HEAT", 2->"COOL"), "temperature", (60, 75), (-2, 2))

        case "lamp" => 
          nextDeviceStateSetting(work, Map(0->"OFF", 1->"ON"), "brightness", (1, 3), (-1, 1))

        case "security-alarm" => 
          nextDeviceStateSetting(work, Map(0->"OFF", 1->"ON"), "level", (1, 5), (-2, 2))

        case _ =>
          // Shouldn't happen (keep state/setting as is)
          log.info("Work Processor -> ALERT: Device type undefined! {}-{}", work.deviceType, work.deviceId)
          DeviceStateSetting(deviceType, state, setting)
      }

      val result = WorkResult(workId, deviceType, deviceId, newStateSetting.state, newStateSetting.setting)
      sender() ! Worker.WorkProcessed(result)
    }

    case _ =>
      log.info("Work Processor -> ALERT: Received unknown message!")
  }

  def nextDeviceStateSetting(
      work: Work, stateMap: Map[Int, String], settingType: String, settingLimit: (Int, Int), changeLimit: (Int, Int)
    ): DeviceStateSetting = {

    val nextState = random.nextInt(0, stateMap.size)

    val nextStateText = if (nextState == work.currState) "Keep state " + stateMap(work.currState) else
      "Switch to " + stateMap(nextState)

    val randomChange = random.nextInt(changeLimit._1, changeLimit._2 + 1)
    val randomSetting = work.currSetting + randomChange

    val nextSettingChange = if (randomChange == 0) 0 else {
      if (randomSetting < settingLimit._1 || randomSetting > settingLimit._2) 0 else randomChange
    }
    val nextSetting = work.currSetting + nextSettingChange

    val nextSettingText = if (nextSettingChange == 0) s"NO $settingType change" else {
      if (nextSettingChange > 0) s"RAISE $settingType by $nextSettingChange" else
        s"LOWER $settingType by $nextSettingChange"
    }

    log.info("Work Processor -> {}-{}: {} | {}", work.deviceType, work.deviceId, nextStateText, nextSettingText)
    DeviceStateSetting(work.deviceType, nextState, nextSetting)
  }
}
