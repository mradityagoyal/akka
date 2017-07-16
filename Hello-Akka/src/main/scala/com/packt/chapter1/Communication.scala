package com.packt.chapter1

import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by addy on 7/16/17.
  */

/**
  * The messages used in the system.
  */
object Messages{
  case class Done(randomNumber: Int)
  case object GiveMeRandomNumber
  case class Start(actorRef: ActorRef)
}

class RandomNumberGenerator extends Actor{
  import scala.util.Random._
  import Messages._
  override def receive: Receive = {
    case GiveMeRandomNumber => sender ! Done(nextInt)
  }
}

class QueryActor extends Actor{
  import Messages._
  override def receive: Receive = {
    case Done(randomNumber) => println(s"The random number received = $randomNumber")
    case Start(actorRef) => {
      println(s"asking for random number from Generator")
      actorRef ! GiveMeRandomNumber
    }
  }
}

object ComminicationApp extends App with ActorSystemTrait {
  import Messages._
  val queryActor = system.actorOf(Props[QueryActor], "QueryActor")
  val generator = system.actorOf(Props[RandomNumberGenerator], "RandomNumberGenerator")

  queryActor ! Start(generator)
}
