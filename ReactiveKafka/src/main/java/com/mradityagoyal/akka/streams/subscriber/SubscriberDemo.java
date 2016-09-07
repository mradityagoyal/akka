package com.mradityagoyal.akka.streams.subscriber;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.mradityagoyal.akka.streams.subscriber.WorkerPool.Worker;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ManualSubscription;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class SubscriberDemo {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

//        final Source<WorkerPoolProtocol.Msg, NotUsed> messages = src.map(s -> WorkerPoolProtocol.msg(s, null));

//        strings.map(i -> WorkerPoolProtocol.msg(i)).runWith(Sink.<WorkerPoolProtocol.Msg> actorSubscriber(WorkerPool.props()), materializer);
        // source from ActorPublisher
        // run the graph

//        final ConsumerSettings<byte[], String> consumerSettings = ConsumerSettings.create(system, new ByteArrayDeserializer(), new StringDeserializer())
//                .withBootstrapServers("localhost:9092").withGroupId("group1").withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        final ManualSubscription subscription = Subscriptions.assignmentWithOffset(new TopicPartition("topic4", 0), 0);
//
//        final Source<ConsumerRecord<byte[], String>, Consumer.Control> src = Consumer.plainSource(consumerSettings, subscription);
        RunnableGraph<Consumer.Control> graph = getSubscibtion("topic4", "group1", Worker.class, system);

        graph.run(materializer);

    }

    /**
     *
     * @param topic
     * @param groupId
     * @param workerClass
     * @param system
     * @return a runnable graph.
     */
    public static RunnableGraph<Consumer.Control> getSubscibtion(String topic, String groupId, Class<?> workerClass, ActorSystem system) {

        final ConsumerSettings<byte[], String> consumerSettings = ConsumerSettings.create(system, new ByteArrayDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092").withGroupId(groupId).withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final ManualSubscription subscription = Subscriptions.assignmentWithOffset(new TopicPartition(topic, 0), 0);
        final Source<ConsumerRecord<byte[], String>, Consumer.Control> src = Consumer.plainSource(consumerSettings, subscription);
        return src.map(s -> s.value()).map(i -> WorkerPoolProtocol.msg(i)).to(Sink.<WorkerPoolProtocol.Msg> actorSubscriber(WorkerPool.props(Worker.class)));

    }

}