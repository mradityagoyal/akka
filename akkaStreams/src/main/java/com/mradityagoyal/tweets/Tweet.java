package com.mradityagoyal.tweets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Tweet {

	public static class Author {
		public final String handle;

		public Author(String handle) {
			this.handle = handle;
		}
	}

	public static class Hashtag {
		public final String name;

		public Hashtag(String name) {
			this.name = name;
		}

	}

	public final Author author;
	public final long timestamp;
	public final String body;

	public Tweet(Author author, long timestamp, String body) {
		this.author = author;
		this.timestamp = timestamp;
		this.body = body;
	}

	public Set<Hashtag> getHashtags() {
		return Arrays.asList(body.split(" ")).stream().filter(x -> x.startsWith("#")).map(tag -> new Hashtag(tag))
				.collect(Collectors.toSet());
	}

	public static final Hashtag AKKA = new Hashtag("#akka");
}
