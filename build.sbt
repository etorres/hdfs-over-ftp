organization := "es.eriktorr.katas"
name := "hdfs-over-ftp"
version := "1.0"

scalaVersion := "2.13.1"

val HadoopVersion = "3.2.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "com.iheart" %% "ficus" % "1.4.7",
  "org.apache.hadoop" % "hadoop-hdfs-client" % HadoopVersion,
  "org.apache.hadoop" % "hadoop-common" % HadoopVersion,
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

Test / envFileName := ".env_test"
envVars in Test := (envFromFile in Test).value

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

//test in assembly := {}
//mainClass in assembly := Some("es.eriktorr.katas.Application")