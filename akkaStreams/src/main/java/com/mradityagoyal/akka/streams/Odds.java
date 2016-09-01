package com.mradityagoyal.akka.streams;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;

public class Odds {
	
	public static void main(String[] args){
		final ActorSystem system = ActorSystem.create("QuickStart");
		final Materializer materializer = ActorMaterializer.create(system);
		Source<Integer, NotUsed> source = Source.range(1, 100000000);
		
		Source<Integer, NotUsed> odd = source.filter(x -> x%2 != 0 );
		
		CompletionStage<Done> result = odd.throttle(2, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
		.runForeach(x -> System.out.println(x), materializer);
		
	}

}
