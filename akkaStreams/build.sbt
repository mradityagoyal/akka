name := """akkaStreams"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "junit"             % "junit"           % "4.12"  % "test",
  "com.novocode"      % "junit-interface" % "0.11"  % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.9" % "test"
)


fork in run := true

EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true