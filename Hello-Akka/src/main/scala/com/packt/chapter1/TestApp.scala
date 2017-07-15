package com.packt.chapter1

import akka.actor.{ActorSystem, Props}

/**
  * Created by addy on 7/14/17.
  */
object TestApp extends App{

  val actorSystem = ActorSystem("TestApp")

  val powerSumActor = actorSystem.actorOf(Props[PowerSumActor])

  powerSumActor ! (100000, 2)

}
