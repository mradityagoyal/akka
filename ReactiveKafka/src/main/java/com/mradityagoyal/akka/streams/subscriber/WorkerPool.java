package com.mradityagoyal.akka.streams.subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.MaxInFlightRequestStrategy;
import akka.stream.actor.RequestStrategy;
import scala.concurrent.duration.Duration;

public class WorkerPool extends AbstractActorSubscriber {

    public static Props props(Class<?> workerClass) {
        return Props.create(WorkerPool.class, workerClass);
    }

    final int MAX_QUEUE_SIZE = 9;

    final List<String> queue = new ArrayList<>();

    // router with three routees , getting messages based on round robin routing logic.
    final Router router;

    @Override
    public RequestStrategy requestStrategy() {
        return new MaxInFlightRequestStrategy(MAX_QUEUE_SIZE) {

            @Override
            public int inFlightInternally() {
                return queue.size();
            }
        };
    }

    public WorkerPool(Class<?> workerClass) {
        final List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            routees.add(new ActorRefRoutee(context().actorOf(Props.create(workerClass))));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
//        @formatter:off
        receive(ReceiveBuilder.match(ActorSubscriberMessage.OnNext.class, on -> on.element() instanceof WorkerPoolProtocol.Msg, onNext -> {
          final WorkerPoolProtocol.Msg msg = (WorkerPoolProtocol.Msg) onNext.element();
          queue.add(msg.msgText);

          if(queue.size() > MAX_QUEUE_SIZE){
              throw new RuntimeException("Queued too many: "+ queue.size());
          }
          router.route(WorkerPoolProtocol.work(msg.msgText), self());
        })
        .match(WorkerPoolProtocol.Reply.class, reply -> {
            final String msgText = reply.msgText;
            queue.remove(msgText);
        })
        .build());
    }

    static class Worker extends AbstractActor{

        public Worker() {
            receive(ReceiveBuilder.
              match(WorkerPoolProtocol.Work.class, work -> {
                System.out.println("work done for :" + work.msgText  );
//                sender().tell(WorkerPoolProtocol.reply(work.msgText), self());
                context().system().scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS), sender(), WorkerPoolProtocol.reply(work.msgText), context().system().dispatcher(), self());

              }).build());
          }
    }

}
