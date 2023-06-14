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
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("twitter", "api"); //describe anything for which we want to read the tweets.
        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1("consumerKey",
                "consumerSecretKey", "accessTokenKey", "secretAccessTokenKey");


        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client hosebirdClient = builder.build();

        return hosebirdClient;  // Attempts to establish a connection.
    }

    public  void run(){

        logger.info("Setup");

        /* Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000); // Specify the size

        Client client = tweetClient(msgQueue);
        client.connect(); //invokes the connection function

        KafkaProducer<String, String> producer = createKafkaProducer();

        // on a different thread, or multiple different threads....
        while (!client.isDone()){

            String msg  = null;

            try {

                msg = msgQueue.poll(5, TimeUnit.SECONDS);

            } catch (InterruptedException e) {

                e.printStackTrace();
                client.stop();
            }

            if(msg != null){

                logger.info(msg);

                producer.send(new ProducerRecord<>("TwitterTopic", null, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception exception) {

                        if(exception != null){

                            logger.error("Something went wrong", exception);
                        }
                    }
                });
            }
        }
        // When the reading is complete, inform logger
        logger.info("This is the end");
    }

    public static void main(String[] args) {

        TweetProducer tweetProducer = new TweetProducer();
        tweetProducer.run();
    }
}