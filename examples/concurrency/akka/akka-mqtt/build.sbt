name := "akka-iot-mqtt"

version := "0.1"

scalaVersion := "2.11.11"
lazy val akkaVersion = "2.4.19"

fork in Test := true

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "org.eclipse.paho"  % "org.eclipse.paho.client.mqttv3"  % "1.0.2",
  "com.sandinh"  % "paho-akka_2.11"  % "1.2.0",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "ch.qos.logback"    % "logback-classic" % "1.1.3",
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.github.etaty" %% "rediscala" % "1.6.0",
  "com.hootsuite" %% "akka-persistence-redis" % "0.6.0")


fork in run := true
