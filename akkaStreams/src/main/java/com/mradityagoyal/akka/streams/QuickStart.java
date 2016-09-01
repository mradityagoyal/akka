package com.mradityagoyal.akka.streams;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import scala.concurrent.duration.Duration;

public class QuickStart {

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("QuickStart");
		final Materializer materializer = ActorMaterializer.create(system);
		final Source<Integer, NotUsed> source = Source.range(1, 100);
		source.runForeach(i -> System.out.println(i), materializer);

		final Source<BigInteger, NotUsed> factorials = source.scan(BigInteger.ONE,
				(acc, next) -> acc.multiply(BigInteger.valueOf(next)));

		final CompletionStage<IOResult> result = factorials.map(num -> ByteString.fromString(num.toString() + "\n"))
				.runWith(FileIO.toPath(Paths.get("factorials.txt")), materializer);

		factorials.map(BigInteger::toString).runWith(lineSink("factorial2.txt"), materializer);
	
		factorials
	    .zipWith(Source.range(0, 99), (num, idx) -> String.format("%d! = %s", idx, num))
	    .throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
	    .runForeach(s -> System.out.println(s), materializer);
	}
	
	public static Sink<String, CompletionStage<IOResult>> lineSink(String filename){
		return Flow.of(String.class)
				   .map(s -> ByteString.fromString(s.toString() + "\n"))
				   .toMat(FileIO.toPath(Paths.get(filename)), Keep.right());
	}

}
