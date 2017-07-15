package com.packt.chapter1

import scala.collection.mutable
import scala.math.{Ordered, floor, min, pow}

/**
  * Created by addy on 7/14/17.
  */
object PowerSumSoln2 {

  def numberOfWays(X:Int,N:Int):Int = {
    val dict = new mutable.HashMap[Int, Set[PowerSumResult]]
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
            println(s"was precalcualted")
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

    def unmemoize(sum: Int, limit:Int): Option[PowerSumResult] = {
      dict.get(sum) match {
        case None => None
        case Some(set) => set.filter(_.maxInSeries == limit).headOption
      }
    }

    def memoize(sum: Int, results: Seq[PowerSumResult]) = {
      dict.get(sum) match {
        case None => dict.put(sum, results.toSet)
        case Some(s) => dict.put(sum, s ++ results)
      }
    }

    numWays(X, Integer.MAX_VALUE).toInt
  }



  case class PowerSumResult(number: Int, maxInSeries: Int, result: Double)

  def main(args: Array[String]) {
    println(numberOfWays(readInt(),readInt()))
  }

}
