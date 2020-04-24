# HDFS over FTP

This is rewrite of the project [hdfs-over-ftp](https://github.com/iponweb/hdfs-over-ftp). I found in this project an opportunity to gain experience and developing my skills in Java-to-Scala migration. However, I reduced the scope excluding some advanced features like modifying user accounts (add, delete FTP users), also only non-SSL is supported.

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

Hadoop name node will use the container hostname to build a download URL. This URL will include an IP address that is relative to the container (e.g `172.17.0.2`). Clients will download the file from the data node pointed by this URL. Therefore, you will need to add an alias to the container in your development environment. You can use the following command to create such an alias in macOS:

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

```shell script
sudo jsvc -home /Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home -debug -nodetach -cp /Users/etorres/KK/commons-daemon-1.2.2/commons-daemon-1.2.2.jar:/Users/etorres/IdeaProjects/hdfs-over-ftp/target/scala-2.13/hdfs-over-ftp-assembly-1.0.jar es.eriktorr.ftp.FtpServerDaemon -config /Users/etorres/KK/application.conf
```

```shell script
ftp ftp://root@localhost:2221
```

In this example, the user root copies the remote file `/user/root/input/yarn-site.xml` to her home directory:

```text
ftp ftp://root@localhost:2221                                              ✔
Trying ::1...
ftp: Can't connect to `::1': Connection refused
Trying 127.0.0.1...
Connected to localhost.
220 Service ready for new user.
331 User name okay, need password for root.
Password:
230 User logged in, proceed.
Remote system type is UNIX.
200 Command TYPE okay.

ftp> cd /user/root/input
250 Directory changed to /user/root/input

ftp> get yarn-site.xml
local: yarn-site.xml remote: yarn-site.xml
229 Entering Passive Mode (|||2222|)
150 File status okay; about to open data connection.
100% |******************************************|  1525       17.85 KiB/s    00:00 ETA
226 Transfer complete.
1525 bytes received in 00:00 (17.35 KiB/s)

ftp> ^D
221 Goodbye.
```

## Additional Resources

* [Setup a Single-Node Hadoop Cluster Using Docker](https://www.alibabacloud.com/blog/setup-a-single-node-hadoop-cluster-using-docker_595278).
* [Creating fat jars for Spark Kafka Streaming using sbt](https://community.cloudera.com/t5/Community-Articles/Creating-fat-jars-for-Spark-Kafka-Streaming-using-sbt/ta-p/246691).
* [How To Use Apache Commons Daemon](https://weinan.io/2017/03/04/how-to-use-commons-daemon.html).