package com.packt.chapter1

import akka.actor.Actor

/**
  * Created by addy on 7/16/17.
  */
class FibonacciActor extends Actor{

  override def receive: Receive = {
    case x: Int => {
      sender ! fib(x)
    }
    case _ => println(s"Send a integer N to calcualte the Nth fibonacci number")
  }

  protected def fib(x: Int): Int = x match {
    case 0 | 1 => 1
    case _ => fib(x -1 ) + fib(x-2)
  }

}
