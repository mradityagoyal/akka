package com.mradityagoyal.akka.streams.kafka;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

public class KafkaProducerDemo {
	final ActorSystem system;
	final Materializer materializer;
	protected final ProducerSettings<byte[], String> producerSettings;

	public KafkaProducerDemo() {
		super();
		system = ActorSystem.create("QuickStart"); // TODO: configure system
													// name.
		materializer = ActorMaterializer.create(system);
		producerSettings = ProducerSettings.create(system, new ByteArraySerializer(), new StringSerializer())
				.withBootstrapServers("localhost:9092"); // TODO: configure
															// kafka port.
	}

	public static void main(String[] args) {

		KafkaProducerDemo demo = new KafkaProducerDemo();
		String msg = "what's goig on .. ha ha. ha.  ";
		Source<String, NotUsed> source = Source.from(Arrays.asList(msg.split(" ")));
		Source<ProducerRecord<byte[], String>, NotUsed> producerRecords = source
				.throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
				.map(s -> new ProducerRecord<>("topic2", s));

		final RunnableGraph<CompletionStage<Done>> graph = producerRecords.toMat(Producer.plainSink(demo.producerSettings), Keep.right());
		final CompletionStage<Done> done = producerRecords.runWith(Producer.plainSink(demo.producerSettings),
				demo.materializer);
		done.thenRun(() -> {
			System.out.println("Completed");
			
		});
		
		final CompletionStage<Done> done2 = graph.run(demo.materializer);
		done2.thenRun(() -> System.out.println("second is done"));

		// RunnableGraph<NotUsed> runnableGraph = source.throttle(1,
		// Duration.create(1, TimeUnit.SECONDS), 1,
		// ThrottleMode.shaping()).to(demo.mapToProducerRecordAndSend("topic2"));
		// NotUsed nu = runnableGraph.run(demo.materializer);
		// System.out.println(nu);

	}

	/**
	 * returns a Sink that consumes elements from a String Source to kafka
	 * topic.
	 * 
	 * @param topic
	 * @return Sink<String, NotUsed> This sink can be used to send elements from
	 *         a String Source to kafka topic.
	 */
	public Sink<String, NotUsed> mapToProducerRecordAndSend(String topic) {
		return Flow.of(String.class).map(s -> new ProducerRecord<byte[], String>(topic, s))
				.to(Producer.plainSink(producerSettings));

	}

	// public Sink<ProducerRecord<byte[], String>, CompletionStage<Done>>
	// getCompletableSink(String topic){
	// final Sink<ProducerRecord<byte[], String>, CompletionStage<Done>> sink1 =
	// Producer.plainSink(producerSettings);
	// final Flow.
	//// return sink1;
	//
	// }

}
