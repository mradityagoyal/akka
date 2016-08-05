package com.mradityagoyal.akkademy.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;

public class JavaPongActor extends AbstractActor {
	
	private final String greeting;
	
	

	public JavaPongActor(String greeting) {
		super();
		this.greeting = greeting;
	}


	public static Props props(String greeting){
		return Props.create(JavaPongActor.class, greeting);
	}

	public PartialFunction receive() {
		return ReceiveBuilder.matchEquals("Ping", s -> sender().tell(greeting, ActorRef.noSender()))
				.matchAny(x -> sender().tell(new Status.Failure(new Exception("unknown message")), self())).build();
	}
}
