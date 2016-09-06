package com.mradityagoyal.akka.streams.publisher;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;


import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.actor.ActorPublisherMessage;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

public class JobManagerKafkaDemo {
	final ActorSystem system;
	final Materializer materializer;
	protected final ProducerSettings<byte[], String> producerSettings;

	public JobManagerKafkaDemo() {
		super();
		system = ActorSystem.create("QuickStart"); // TODO: configure system
													// name.
		materializer = ActorMaterializer.create(system);
		producerSettings = ProducerSettings.create(system, new ByteArraySerializer(), new StringSerializer())
				.withBootstrapServers("localhost:9092"); // TODO: configure
															// kafka port.
	}

	public static void main(String[] args) {

		JobManagerKafkaDemo demo = new JobManagerKafkaDemo();
		JobManagerProtocol protocol = new JobManagerProtocol();
		//source from ActorPublisher
		final Source<JobManagerProtocol.Message, ActorRef> jobManagerSource = Source.actorPublisher(JobManager.props());
		
		//throtthe the source
		final Source<JobManagerProtocol.Message, ActorRef> throttledRecords = jobManagerSource
				.throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping());
		
		//map to producerRecords. TODO: send topic
		final Source<ProducerRecord<byte[], String>, ActorRef> throttledProducerRecords = throttledRecords.map(msg -> new ProducerRecord<>("topic2", msg.payload));

		//create runnable graph
		final RunnableGraph<ActorRef> graph = throttledProducerRecords.to(Producer.plainSink(demo.producerSettings));
		//run the graph
		final ActorRef ref = graph.run(demo.materializer);
		
		ref.tell(protocol. new Message("hello 1 "), ActorRef.noSender());
		ref.tell(protocol. new Message("hello 2 "), ActorRef.noSender());
		ref.tell(protocol. new Message("hello 3 "), ActorRef.noSender());
		ref.tell(protocol. new Message("hello 4"), ActorRef.noSender());
		
		
//		demo.system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), ref, ActorPublisherMessage.cancelInstance(), demo.system.dispatcher());
//		ref.tell(ActorPublisherMessage.cancelInstance(), ActorRef.noSender());
		Scheduler sch = demo.system.scheduler();
//		(Duration.create(30, TimeUnit.SECONDS), ref,  ActorPublisherMessage.cancelInstance(), null)
		sch.scheduleOnce(Duration.create(10, TimeUnit.SECONDS), ref, ActorPublisherMessage.cancelInstance(), demo.system.dispatcher(), null);
		sch.scheduleOnce(Duration.create(15, TimeUnit.SECONDS), new Runnable() {
			@Override
			public void run() {
				System.out.println("shutting down system");
				demo.system.stop(ref);
				demo.system.shutdown();
			}
		}, demo.system.dispatcher());



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
