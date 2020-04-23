organization := "es.eriktorr.katas"
name := "hdfs-over-ftp"
version := "1.0"

scalaVersion := "2.13.1"

val HadoopVersion = "2.6.5"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "com.iheart" %% "ficus" % "1.4.7",
  "org.apache.ftpserver" % "ftpserver-core" % "1.1.1",
  "org.apache.hadoop" % "hadoop-hdfs" % HadoopVersion, // replace with "hadoop-hdfs-client" for version "3.2.1"
  "org.apache.hadoop" % "hadoop-common" % HadoopVersion,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "commons-net" % "commons-net" % "3.6" % Test,
  "com.github.pathikrit" %% "better-files" % "3.8.0" % Test
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

//jacocoReportSettings := JacocoReportSettings().withThresholds(
//  JacocoThresholds(
//    instruction = 80,
//    method = 100,
//    branch = 100,
//    complexity = 100,
//    line = 90,
//    clazz = 100
//  )
//)

test in assembly := {}
mainClass in assembly := Some("es.eriktorr.katas.FtpServerDaemon")

assemblyMergeStrategy in assembly := {
  case PathList("com", "sun", xs @ _*) => MergeStrategy.last
  case PathList("commons-beanutils", xs @ _*) => MergeStrategy.last
  case PathList("commons-cli", xs @ _*) => MergeStrategy.last
  case PathList("commons-collections", xs @ _*) => MergeStrategy.last
  case PathList("commons-io", xs @ _*) => MergeStrategy.last
  case PathList("io", "netty", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("javax", "xml", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("org", "codehaus", xs @ _*) => MergeStrategy.last
  case PathList("org", "fusesource", xs @ _*) => MergeStrategy.last
  case PathList("org", "mortbay", xs @ _*) => MergeStrategy.last
  case PathList("org", "tukaani", xs @ _*) => MergeStrategy.last
  case PathList("xerces", xs @ _*) => MergeStrategy.last
  case PathList("xmlenc", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
