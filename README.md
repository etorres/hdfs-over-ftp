# HDFS over FTP

This is rewrite of the project [hdfs-over-ftp](https://github.com/iponweb/hdfs-over-ftp). I found in this project an opportunity to gain experience and developing my skills in Java-to-Scala migration. However, I reduced the scope excluding some advanced features like modifying user accounts (add, delete FTP users), also only non-SSL is supported.

Regarding 

## Configuring your environment

Pull the Hadoop docker image:

```shell script
docker pull sequenceiq/hadoop-docker:2.6.0
```

You can check the image by starting a container locally in your laptop:

```shell script
docker run -p 50070:50070 -p 50010:50010 -p 8030:8030 -it sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -bash
```

__WARNING__

Hadoop name node will use the container hostname to build a download URL. This URL will include an IP address that is relative to the container (e.g `172.17.0.2`). Clients will download the file from the data node pointed by this URL. Therefore, you will need to add an alias to the container in your development environment:

```shell script
sudo ifconfig lo0 alias 172.17.0.2
```

You can always remove this alias with the following command:

```shell script
sudo ifconfig lo0 -alias 172.17.0.2
```

You can find out all the ports mapped with the docker port command (you can find the container id as usual with `docker ps`):

```text
~ docker port 289c381868ed
50010/tcp -> 0.0.0.0:50010
50070/tcp -> 0.0.0.0:50070
9000/tcp -> 0.0.0.0:9000
```

You can make sure all the daemons are up and running by executing the command `jps` in the console of the container.

Useful commands:

```shell script
/usr/local/hadoop/bin/hadoop fs -cat hdfs://localhost:8030/user/root/hdfs-site.xml

/usr/local/hadoop/bin/hdfs getconf -confKey fs.defaultFS
```

## Build and test this project with specific Java version

```shell script
jenv exec sbt clean test
```

## List dependencies of this project

```shell script
jenv exec sbt dependencyTree
```

```shell script
jenv exec sbt dependencyBrowseGraph
```

## Code coverage

```shell script
jenv exec sbt jacoco
```

## Distribution

```shell script
jenv exec sbt assembly
```

## Additional Resources

* [Setup a Single-Node Hadoop Cluster Using Docker](https://www.alibabacloud.com/blog/setup-a-single-node-hadoop-cluster-using-docker_595278).
* [Creating fat jars for Spark Kafka Streaming using sbt](https://community.cloudera.com/t5/Community-Articles/Creating-fat-jars-for-Spark-Kafka-Streaming-using-sbt/ta-p/246691).