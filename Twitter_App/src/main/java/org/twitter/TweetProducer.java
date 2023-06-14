package org.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
    For this use: https://github.com/twitter/hbc
    */
public class TweetProducer {

    Logger logger = LoggerFactory.getLogger(TweetProducer.class.getName());

    // This is API key from Twitter
    String consumerKey = "H1MStpybqe0Gy5PTPYZlwLvrJ";

    // This is secret API key from Twitter
    String consumerSecretKey = "xpUlDW0njWFHo5js0MKOhzEI5QkHfcmPCQwIE4Bdc0Rlj61G3l";

    // This is access token key from Twitter
    String accessTokenKey = "813022289713364992-WMWDqw3M4qjnqEx0XgSD4ENRL6yDYP0";

    // This is secret access token key from Twitter
    String secretAccessTokenKey = "5ol4Ym9aRi3ZVSo1w1LQMg6ASW10FM02y4max1pEzhPti";

    // Constructor to invoke producer function
    public TweetProducer() {
    }

    // Creating kafka producer and producer properties
    public KafkaProducer<String, String> createKafkaProducer(){

        String bootstrapServers = "localhost:9092";

        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producerOne = new KafkaProducer<>(properties);

        return producerOne;
    }

    public Client tweetClient(BlockingQueue<String> msgQueue){

        // From: https://github.com/twitter/hbc

        /* Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */

    }

    public  void run(){

        logger.info("Setup");

        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000); // Specify the size


    }

    public static void main(String[] args) {

        TweetProducer tweetProducer = new TweetProducer();

    }
}