# HDFS over FTP

__Project description__

/usr/local/hadoop/bin/hadoop fs -cat hdfs://localhost:8030/user/root/hdfs-site.xml

/usr/local/hadoop/bin/hdfs getconf -confKey fs.defaultFS

## Configuring your environment

Pull the Hadoop docker image:

```shell script
docker pull sequenceiq/hadoop-docker:2.7.1
```

You can check the image by starting a container locally in your laptop:

```shell script
docker run -p 50070:50070 -p 8030:8030 -it sequenceiq/hadoop-docker:2.7.1 /etc/bootstrap.sh -bash
```

You can find out all the ports mapped with the docker port command (you can find the container id as usual with `docker ps`):

```text
~ docker port 289c381868ed
50070/tcp -> 0.0.0.0:50070
```

You can make sure all the daemons are up and running by executing the command `jps` in the console of the container.

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
