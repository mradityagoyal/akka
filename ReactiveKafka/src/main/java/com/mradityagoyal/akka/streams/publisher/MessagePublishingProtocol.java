package com.mradityagoyal.akka.streams.publisher;

public class JobManagerProtocol {

	final public class Message {
		public final String payload;

		public Message(String payload) {
			this.payload = payload;
		}

	}

	public static class JobAcceptedMessage {

		@Override
		public String toString() {
			return "JobAccepted";
		}
	}

	public static final JobAcceptedMessage JOB_ACCEPTED = new JobAcceptedMessage();

	public static class JobDeniedMessage {
		@Override
		public String toString() {
			return "JobDenied";
		}
	}
	public static final JobDeniedMessage JOB_DENIED = new JobDeniedMessage();

}
