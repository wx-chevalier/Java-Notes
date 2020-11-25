package akkaiot

import java.io.Serializable

case class Work(workId: String, deviceType: String, deviceId: String, currState: Int, currSetting: Int) extends Serializable

case class WorkResult(workId: String, deviceType: String, deviceId: String, nextState: Int, nextSetting: Int) extends Serializable
