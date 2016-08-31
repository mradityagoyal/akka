name := """akkaStreams"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  // Uncomment to use Akka
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9",
  "com.typesafe.akka" % "akka-stream_2.11" % "2.4.9",
   "com.typesafe.akka" % "akka-stream_2.11" % "2.4.9" classifier "javadoc",
  "junit"             % "junit"           % "4.12"  % "test",
  "com.novocode"      % "junit-interface" % "0.11"  % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test"
)


fork in run := true