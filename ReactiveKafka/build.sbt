name := """ReactiveKafka"""

version := "1.0"

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-RC2"
)

fork in run := true

EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true
