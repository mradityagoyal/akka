package com.goyal.addy.powersum.akka

import akka.actor.Actor

import scala.collection.mutable
import scala.math._

/**
  * Created by addy on 7/14/17.
  */
class PowerSumActor extends Actor {

  override def receive: Receive = {
    case (x: Int, y: Int) => {
      println(s"Number of ways you can write $x as a sum of unique digits raise to power of $y is ${numberOfWays(x, y)}")
      //      print(dict)
    }
  }


  val dict = new mutable.HashMap[Int, Map[Int, PowerSumResult]]


  case class PowerSumResult(number: Int, maxInSeries: Int, result: Double) extends Ordered[PowerSumResult] {

    def compare(that: PowerSumResult): Int = this.maxInSeries compare that.maxInSeries
  }


  def numberOfWays(X: Int, N: Int): Double = {
    def numWays(sum: Int, maximum: Int): Double = sum match {
      case x if x < 1 => 0
      case _ => {
        val flooredRoot = floor(pow(sum, 1.0 / N)).toInt
        if (maximum > flooredRoot) {
          numWays(sum, flooredRoot)
        } else {
          val limit = min(maximum, flooredRoot)
          val precalc = unmemoize(sum, limit)
          if (precalc.isDefined) {
//            println(s"was precalcualted")
            precalc.get.result
          } else {
            val range = limit to 1 by -1
            val results = range zip range.map(x => {
              val y = pow(x, N).toInt
              if (y == sum) 1 else numWays(sum - y, x - 1)
            })

            val zero = PowerSumResult(sum, 0, 0)

            def op(tuple : (Int, Double), that: PowerSumResult): PowerSumResult = {
              PowerSumResult(that.number, tuple._1, that.result + tuple._2)
            }
            val list = results.scanRight(zero)(op)

            //try to remember
            memoize(sum, list)
            list.head.result
          }
        }
      }
    }
    numWays(X, Integer.MAX_VALUE)
  }

  private def unmemoize(sum: Int, limit:Int): Option[PowerSumResult] = {
    dict.get(sum) match {
      case None => None
      case Some(map) => map.get(limit)
    }
  }

  private def memoize(sum: Int, results: Seq[PowerSumResult]) = {
    val resultsMap = results.map(psr => psr.maxInSeries -> psr).toMap
    dict.get(sum) match {
      case None => {
        dict.put(sum, resultsMap)
      }
      case Some(map) => {
        dict.put(sum, map ++ resultsMap)
      }
    }
  }
}
