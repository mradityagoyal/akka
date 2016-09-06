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
import akka.stream.actor.ActorPublisherMessage;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

public class JobManagerKafkaDemo {

    final ActorSystem system;
    final Materializer m_materializer;
    protected final ProducerSettings<byte[], String> m_producerSettings;

    public JobManagerKafkaDemo() {
        super();
        system = ActorSystem.create("QuickStart"); // TODO: configure system
                                                   // name.
        m_materializer = ActorMaterializer.create(system);
        m_producerSettings = ProducerSettings.create(system, new ByteArraySerializer(), new StringSerializer()).withBootstrapServers("localhost:9092"); // TODO:
                                                                                                                                                        // configure
                                                                                                                                                        // kafka
                                                                                                                                                        // port.
    }

    public static void main(String[] args) {
        final JobManagerKafkaDemo demo = new JobManagerKafkaDemo();
        final MessagePublishingProtocol protocol = new MessagePublishingProtocol();
        // source from ActorPublisher
        // run the graph
        final ActorRef ref = demo.getMessageActorPublisher("topic2");
        final Scheduler sch = demo.system.scheduler();
        ref.tell(protocol.new Message("hello 1 "), ActorRef.noSender());
        ref.tell(protocol.new Message("hello 2 "), ActorRef.noSender());
        ref.tell(protocol.new Message("hello 89"), ActorRef.noSender());
        ref.tell(protocol.new Message("hello 4"), ActorRef.noSender());

        sch.scheduleOnce(Duration.create(5, TimeUnit.SECONDS), ref, protocol.new Message("This seems to work."), demo.system.dispatcher(), null);

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
     *
     * @param topic - the topic to publish mesages to.
     * @return a actor that publishes any messages it receives to kafka topic
     */
    private ActorRef getMessageActorPublisher(String topic) {
        // create source.
        final Source<MessagePublishingProtocol.Message, ActorRef> jobManagerSource = Source.actorPublisher(MessageActorPublisher.props());
        // map to producer records
        final Source<ProducerRecord<byte[], String>, ActorRef> producerRecords = jobManagerSource.map(msg -> new ProducerRecord<>(topic, msg.payload));
        // connect to kafka producer sink
        final RunnableGraph<ActorRef> graph = producerRecords.to(Producer.plainSink(m_producerSettings));
        // run and return
        return graph.run(m_materializer);
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
        return Flow.of(String.class).map(s -> new ProducerRecord<byte[], String>(topic, s)).to(Producer.plainSink(m_producerSettings));

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
