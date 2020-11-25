package akkaiot

import scala.util.Try
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props, ActorRef, PoisonPill }
import akka.actor.{ RootActorPath, ActorPath, AddressFromURIString, Identify, ActorIdentity }
import akka.pattern._
import akka.cluster.client.{ClusterClientReceptionist, ClusterClientSettings, ClusterClient}
import akka.cluster.singleton.{ClusterSingletonManagerSettings, ClusterSingletonManager}
import akka.japi.Util.immutableSeq
import akka.util.Timeout

import com.sandinh.paho.akka._
import com.sandinh.paho.akka.MqttPubSub._

object Main {

  def main(args: Array[String]): Unit = {

    val defaultDevices = 20

    if (args.isEmpty) {
      startBackend(2551, "backend")
      Thread.sleep(5000)

      startBackend(2552, "backend")

      startWorker(0)
      Thread.sleep(5000)

      startIot(3001, defaultDevices)

    } else {
      val port = args(0).toInt
      val numOfDevices = if (args.length > 1) args(1).toInt else defaultDevices

      if (2000 <= port && port <= 2999)
        startBackend(port, "backend")
      else if (3000 <= port && port <= 3999)
        startIot(port, numOfDevices)
      else if (port == 0)
        startWorker(port)
    }
  }

  def workTimeout = 10.seconds

  def startBackend(port: Int, role: String): Unit = {
    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
      withFallback(ConfigFactory.load())
    val system = ActorSystem("ClusterSystem", conf)

    system.actorOf(
      ClusterSingletonManager.props(
        Master.props(workTimeout),
        PoisonPill,
        ClusterSingletonManagerSettings(system).withRole(role)
      ),
      "master")
  }

  def startIot(port: Int, numOfDevices: Int): Unit = {
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load("iotmanager"))
    val system = ActorSystem("IotSystem", conf)
    val initialContacts = immutableSeq(conf.getStringList("contact-points")).map {
      case AddressFromURIString(addr) => RootActorPath(addr) / "system" / "receptionist"
    }.toSet

    val clusterClient = system.actorOf(
      ClusterClient.props(
        ClusterClientSettings(system)
          .withInitialContacts(initialContacts)),
      "clusterClient")

    val mqttPubSub = system.actorOf(Props(classOf[MqttPubSub], MqttConfig.psConfig))

    val iotManager = system.actorOf(IotManager.props(clusterClient, numOfDevices, mqttPubSub), "iot-manager")

    // Start ResultProcessor with random port
    startResultProcessor(
      resPort = 0,
      iotPath = ActorPath.fromString(s"akka.tcp://IotSystem@127.0.0.1:${port}/user/iot-manager")
    )
  }

  def startWorker(port: Int): Unit = {
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load("worker"))
    val system = ActorSystem("WorkerSystem", conf)
    val initialContacts = immutableSeq(conf.getStringList("contact-points")).map {
      case AddressFromURIString(addr) => RootActorPath(addr) / "system" / "receptionist"
    }.toSet

    val clusterClient = system.actorOf(
      ClusterClient.props(
        ClusterClientSettings(system)
          .withInitialContacts(initialContacts)),
      "clusterClient")

    system.actorOf(Worker.props(clusterClient, Props[WorkProcessor]), "worker")
  }

  def startResultProcessor(resPort: Int, iotPath: ActorPath): Unit = {
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + resPort).
      withFallback(ConfigFactory.load())
    val system = ActorSystem("ClusterSystem", conf)

    system.actorOf(ResultProcessor.props(iotPath), "result-processor")
  }
}
