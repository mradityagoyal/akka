package com.packt.chapter1

import akka.actor.ActorSystem

/**
  * Created by addy on 7/16/17.
  */
trait ActorSystemTrait {

  val system = ActorSystem("testActorSystem")


}
