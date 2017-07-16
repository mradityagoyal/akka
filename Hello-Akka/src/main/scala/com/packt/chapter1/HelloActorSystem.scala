package com.packt.chapter1

import akka.actor.ActorSystem

/**
  * Created by addy on 7/14/17.
  */
object HelloActorSystem extends App{

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem)

}
