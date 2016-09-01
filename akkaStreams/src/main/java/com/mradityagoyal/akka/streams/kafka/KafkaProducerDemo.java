package com.mradityagoyal.akka.streams.kafka;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

public class KafkaProducerDemo {
	final ActorSystem system;
	final Materializer materializer ;
	protected final ProducerSettings<byte[], String> producerSettings;
	
	
	public KafkaProducerDemo() {
		super();
		system = ActorSystem.create("QuickStart"); //TODO: configure system name. 
		materializer = ActorMaterializer.create(system);
		producerSettings = ProducerSettings
				  .create(system, new ByteArraySerializer(), new StringSerializer())
				  .withBootstrapServers("localhost:9092"); //TODO: configure kafka port.  
	}


	public static void main(String[] args){
		
		KafkaProducerDemo demo = new KafkaProducerDemo();
		String msg = "irst we use the scan combinator to run a computation over the whole stream: starting with the number 1 (BigInteger.ONE) we multiple by each of the incoming numbers, one after the other; the scan operationemits the initial value and then every calculation result. This yields the series of factorial numbers which we stash away as a Source for later reuse—it is important to";
		Source<String, NotUsed> source = Source.from(Arrays.asList(msg.split(" ")));
		RunnableGraph<NotUsed> runnableGraph = source.throttle(1, Duration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping()).to(demo.mapToProducerRecordAndSend("topic2"));
		runnableGraph.run(demo.materializer);
		
		
		
	}
	
	/**
	 * returns a Sink that consumes elements from a String Source to kafka topic. 
	 * @param topic
	 * @return Sink<String, NotUsed> This sink can be used to send elements from a String Source to kafka topic. 
	 */
	public Sink<String, NotUsed> mapToProducerRecordAndSend(String topic){
		 return Flow.of(String.class).map(s -> new ProducerRecord<byte[], String>(topic, s)).to(Producer.plainSink(producerSettings));
				
	}

}
