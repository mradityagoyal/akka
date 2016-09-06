package com.mradityagoyal.akka.streams.publisher;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorPublisher;
import akka.stream.actor.ActorPublisherMessage;

public class JobManager extends AbstractActorPublisher<JobManagerProtocol.Message> {

	public static Props props() {
		return Props.create(JobManager.class);
	}

	private final int MAX_BUFFER_SIZE = 100;
	private final List<JobManagerProtocol.Message> buf = new ArrayList<>();

	public JobManager() {
		// @formatter:Off
		receive(ReceiveBuilder.match(JobManagerProtocol.Message.class, msg -> buf.size() == MAX_BUFFER_SIZE, msg -> {
			sender().tell(JobManagerProtocol.JOB_DENIED, self());
		})
		.match(JobManagerProtocol.Message.class, msg -> {
			sender().tell(JobManagerProtocol.JOB_ACCEPTED, self());
			if (buf.isEmpty() && totalDemand() > 0) {
				onNext(msg);
			} else {
				buf.add(msg);
				deliverBuf();
			}
		})
		.match(ActorPublisherMessage.Request.class, request -> deliverBuf())
		.match(ActorPublisherMessage.Cancel.class, cancel -> {
			System.out.println("Cancelling stuff");
			context().stop(self());
		})
		.build());
		// @formatter:on
	}

	void deliverBuf() {
		while (totalDemand() > 0) {
			/*
			 * totalDemand is a Long and could be larger than what buf.splitAt
			 * can accept
			 */
			if (totalDemand() <= Integer.MAX_VALUE) {
				final List<JobManagerProtocol.Message> took = buf.subList(0, Math.min(buf.size(), (int) totalDemand()));
				took.forEach(this::onNext);
				buf.removeAll(took);
				break;
			} else {
				final List<JobManagerProtocol.Message> took = buf.subList(0, Math.min(buf.size(), Integer.MAX_VALUE));
				took.forEach(this::onNext);
				buf.removeAll(took);
			}
		}
	}

}
