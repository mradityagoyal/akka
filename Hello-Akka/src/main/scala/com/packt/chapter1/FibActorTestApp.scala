package com.packt.chapter1

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by addy on 7/16/17.
  */
object FibActorTestApp extends App with ActorSystemTrait{

  implicit val timeout = Timeout(10 seconds)
  val fibActor = system.actorOf(Props[FibonacciActor])

  val N: Int = 10
  val future: Future[Int] = (fibActor ? N).mapTo[Int]

  val result = Await.result(future, 10 second)

  println(s"The ${N}th fibonacci number is $result")

}
