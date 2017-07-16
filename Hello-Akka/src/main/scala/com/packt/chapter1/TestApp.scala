package com.packt.chapter1

import akka.actor.{ActorSystem, Props}
import com.goyal.addy.powersum.akka.PowerSumActor

/**
  * Created by addy on 7/14/17.
  */
object TestApp extends App{

  val actorSystem = ActorSystem("TestApp")

  val powerSumActor = actorSystem.actorOf(Props[PowerSumActor])

  powerSumActor ! (1000, 2)

}
