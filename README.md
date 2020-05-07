# HDFS over FTP

[![Build Status](https://circleci.com/gh/etorres/hdfs-over-ftp.svg?style=svg)](https://circleci.com/gh/etorres/hdfs-over-ftp)

This is rewrite of the project [hdfs-over-ftp](https://github.com/iponweb/hdfs-over-ftp). I found in this project an opportunity to gain experience and developing my skills in Java-to-Scala migration. However, I reduced the scope excluding some advanced features like modifying user accounts (add, delete FTP users), also only non-SSL is supported.

On the other hand, I also added a chroot jail mode that can be optionally enabled on start-up via configuration properties.

## Getting Started

Extract the distribution package:

```shell script
unzip hdfs-over-ftp-1.0.0.zip -d <destination_dir>
```

Run `hdfs-over-ftp` using the native launchers of your platform (Linux/macOS or Windows):

```shell script
./bin/hdfs-over-ftp -J-Xms4G -J-Xmx4G \
  -java-home /Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home \
  -config <path-to-configuration>/application.conf
```

The FTP server will start, and you should see a logging message like the following:

```text
INFO main ftp.FtpServerApplication$:7 - hdfs-over-ftp, version 1.0.0, built at 2020-05-03 20:53:07.954
INFO main impl.DefaultFtpServer:89 - FTP server started
INFO main ftp.FtpServerApplication$:27 - FTP server listening in localhost/2221 and connected to hdfs://localhost:9000
```

You can connect to `hdfs-over-ftp` using a FTP server. In this example, the user root copies the remote file `/user/root/input/yarn-site.xml` to her home directory:

```text
$ ftp ftp://root@localhost:2221
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

## Configuring `hdfs-over-ftp`

A default configuration file will be included with the distribution. You can check the [application.conf](src/main/resources/application.conf) for additional information about configuration properties.

### Using chroot jail

Changing working directory to a directory below the home directory will result in an error `550`:

```text
ftp> cd ..
550 No such directory.
```

Likewise, getting a file below the home directory will result in the following error:

```text
ftp> get /user/hadoop/libs/udf.jar
local: /user/hadoop/libs/udf.jar remote: /user/hadoop/libs/udf.jar
ftp: Can't access `/user/hadoop/libs/udf.jar': No such file or directory
```

### Limiting the maximum number of files listed

Listing a HDFS directory with many files may cause a long delay due to a heavy postprocessing of the information of the files. You can limit the maximum number of files that can be listed. Listing a directory with more files than the configured limit will fail with the following error:

```text
ftp> ls
229 Entering Passive Mode (|||2223|)
150 File status okay; about to open data connection.
550 Requested action not taken.
```

## Contributing to the project

### Configuring your environment

Pull the Hadoop docker image:

```shell script
docker pull sequenceiq/hadoop-docker:2.6.0
```

You can check the image by starting a container locally in your laptop:

```shell script
docker run -p 50070:50070 -p 50010:50010 -p 9000:9000 -it sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -bash
```

Or if you prefer, you can run the container detached from the shell:

```shell script
docker run -p 50070:50070 -p 50010:50010 -p 9000:9000 -d sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -d
```

__WARNING__

Hadoop name node will use the container hostname to build a download URL. This URL will include an IP address that is relative to the container (e.g `172.17.0.2`). Clients will download the file from the data node pointed by this URL. Therefore, you will need to add an alias to the container in your development environment. You can use the following command to create such an alias in macOS:

```shell script
sudo ifconfig lo0 alias 172.17.0.2
```

You can always remove this alias later with the following command:

```shell script
sudo ifconfig lo0 -alias 172.17.0.2
```

You can access the Hadoop manager in your browser in the following URL: http://localhost:50070/

You can find out all the ports mapped with the docker port command (you can find the container _id_ with `docker ps`):

```text
~ docker port 289c381868ed
9000/tcp -> 0.0.0.0:9000
50010/tcp -> 0.0.0.0:50010
50070/tcp -> 0.0.0.0:50070
```

You can make sure all the daemons are up and running by executing the command `jps` in the console of the container.

Useful commands:

```shell script
/usr/local/hadoop/bin/hadoop fs -cat hdfs://localhost:8030/user/root/hdfs-site.xml

/usr/local/hadoop/bin/hdfs getconf -confKey fs.defaultFS
```

### Building and testing this project with specific Java version (using [jenv](https://www.jenv.be/))

This command will run the tests with enabled coverage as configured in [build.sbt](build.sbt):

```shell script
jenv exec sbt clean test
```

#### To generate the coverage reports run

```shell script
jenv exec sbt coverageReport
```

### Listing dependencies of this project

```shell script
jenv exec sbt dependencyTree
```

```shell script
jenv exec sbt dependencyBrowseGraph
```

## Building distribution from source code

```shell script
jenv exec sbt universal:packageBin
```

## Additional resources

* [Setup a Single-Node Hadoop Cluster Using Docker](https://www.alibabacloud.com/blog/setup-a-single-node-hadoop-cluster-using-docker_595278).
* [Hadoop: Setting up a Single Node Cluster](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html).
* [Debugging CI/CD pipelines with SSH access](https://circleci.com/blog/debugging-ci-cd-pipelines-with-ssh-access/).

### Configure Java 11 in your work directory

```text
~ jenv versions
  system
* 1.8 (set by /home/username/.jenv/version)
  1.8.0.242
  11.0
  11.0.6
```

```shell script
jenv local 11.0
```
