package com.packt.chapter1

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by addy on 7/14/17.
  */
class SummingActor(var sum: Int) extends Actor{

  override def receive: Receive = {
    case x: Int => sum = sum + x
      println(s"My state as sum is $sum")
    case _ => println("I don't know what you are talking about")
  }

}

object BeaviorAndState extends App{
  val actorSystem = ActorSystem("BehaviroAndState")
  val actor = actorSystem.actorOf(Props(classOf[SummingActor], 0), "summingActor")

  println(actor.path)

  (1 to 5).par.foreach (i => actor ! i)
  actor ! "Hello"
}
