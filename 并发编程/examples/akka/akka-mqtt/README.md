# IoT & Akka Actor Systems v.2

This is a Scala-based application using Akka Actor systems and MQTT Pub-Sub messaging on a scalable fault-tolerant distributed platform to simulate individual IoT (Internet of Things) devices, each maintaining its own internal state and setting.  It's an expanded version (hence v.2) of another [Akka-IoT application](https://github.com/oel/akka-iot-mqtt).
For an overview of the application, please visit [Genuine Blog](http://blog.genuine.com/2017/07/scala-iot-systems-with-akka-actors-ii/).

## Running on a single JVM
To run the application on a single JVM with the default 20 devices, simply git-clone the repo to a local disk, open up a command line terminal, run the following command and observe the console output:

1. Start a Redis server accessible to the master cluster to serve as the persistence journal:
```bash
$ redis-server [/path/to/conf]
```
2. Launch the master cluster with 2 seed nodes, IoT actor system and Worker actor system:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main"
```

## Running on separate JVMs
To run the application on separate JVMs with a given number of devices, please proceed as follows:

git-clone the repo to a local disk, open up separate command line terminals and launch the different components on separate terminals:

1. Start the Redis server which serves as the persistence journal accessible to the master cluster:
```bash
$ redis-server [/path/to/conf]
```
2. Launch the master cluster seed node with persistence journal:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main 2551"
```
3. Launch additional master cluster seed node:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main 2552"
```
4. Launch the IoT node:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main 3001 [numOfDevices]"
```
5. Launch a Worker node:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main 0"
```
6. Launch additional Worker node:
```bash
$ {project-root}/bin/sbt "runMain akkaiot.Main 0"
```

The optional `numOfDevices` parameter, if not provided, defaults to 20.

To scale up work processing service for the IoT devices, start up additional worker (and/or master) nodes.

To test fault tolerance in the master cluster, stop a master node and observe how the cluster fails over to the next oldest master node.
