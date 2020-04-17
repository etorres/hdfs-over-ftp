organization := "es.eriktorr.katas"
name := "hdfs-over-ftp"
version := "1.0"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "org.apache.hadoop" % "hadoop-hdfs" % "3.2.1",
  "org.apache.ftpserver" % "ftpserver-core" % "1.1.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "commons-net" % "commons-net" % "3.6" % Test
)

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-Xlint",
  "-deprecation",
  "-unchecked"
)

javacOptions ++= Seq(
  "-g:none",
  "-source",
  "11",
  "-target",
  "11",
  "-encoding",
  "UTF-8"
)

scalafmtOnCompile := true

wartremoverErrors ++= Warts.unsafe
wartremoverWarnings ++= Warts.unsafe

jacocoReportSettings := JacocoReportSettings().withThresholds(
  JacocoThresholds(
    instruction = 80,
    method = 100,
    branch = 100,
    complexity = 100,
    line = 90,
    clazz = 100
  )
)
