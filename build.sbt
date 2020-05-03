maintainer := "etserrano@gmail.com"
organization := "es.eriktorr"
name := "hdfs-over-ftp"
version := "1.0.0"

scalaVersion := "2.13.2"

val HadoopVersion = "3.2.1"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "com.iheart" %% "ficus" % "1.4.7",
  "org.apache.ftpserver" % "ftpserver-core" % "1.1.1",
  "org.apache.hadoop" % "hadoop-hdfs-client" % HadoopVersion,
  "org.apache.hadoop" % "hadoop-common" % HadoopVersion,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "commons-net" % "commons-net" % "3.6" % Test,
  "com.github.pathikrit" %% "better-files" % "3.8.0" % Test,
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.4.1" % Test
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

coverageMinimum := 80
coverageFailOnMinimum := true
coverageEnabled := true
coverageExcludedPackages := "es\\.eriktorr\\.ftp\\.BuildInfo"

enablePlugins(JavaServerAppPackaging)

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "es.eriktorr.ftp",
    buildInfoOptions := Seq(BuildInfoOption.BuildTime)
  )
