name := """ReactiveKafka"""

version := "1.0"

scalaVersion := "2.11.7"

val akkaVersion = "2.3.14"
val akkaStreamVersion = "2.0"
val kafkaVersion = "0.9.0.0"

libraryDependencies ++= Seq(
  "com.softwaremill.reactivekafka" %% "reactive-kafka-core" % "0.9.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.12",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)

fork in run := true
