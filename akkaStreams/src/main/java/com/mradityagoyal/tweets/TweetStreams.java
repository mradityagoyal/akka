package com.mradityagoyal.tweets;

import java.util.ArrayList;

import com.mradityagoyal.tweets.Tweet.Author;
import com.mradityagoyal.tweets.Tweet.Hashtag;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class TweetStreams {
	
	public static void main(String[] args){
		final ActorSystem system = ActorSystem.create("reactive-tweets");
		final Materializer materializer = ActorMaterializer.create(system);
		
//		TODO: provide tweets
		Source<Tweet, NotUsed> tweets = null;
		
		Source<Author, NotUsed> authors = tweets.filter( t -> t.getHashtags().contains(Tweet.AKKA))
				.map(t -> t.author);
		
		Source<Hashtag, NotUsed> hashtags = tweets.mapConcat(t -> new ArrayList<Hashtag>(t.getHashtags()));
		
		authors.runWith(Sink.foreach(a -> System.out.println(a)), materializer);
		
		
	}

}
