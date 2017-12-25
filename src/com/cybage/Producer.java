/*
 * Kafka Producer Program
 * @Shraddha kulkarni
 */

package com.cybage;

import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Producer {
	private static Scanner in;

	public static void main(String[] argv) throws Exception {
		// if command line arguments are not passed
		if (argv.length != 1) {
			System.err.println("Please specify 1 parameters ");
			System.exit(-1);
		}
		// Accept topic name by command line arguments
		String topicName = argv[0];

		// Accept the msgs to emit by producer
		in = new Scanner(System.in);
		System.out.println("Enter message(type exit to quit)");

		// ProducerConfig class defines all the different properties available.

		Properties configProperties = new Properties();

		// BOOTSTRAP_SERVERS_CONFIG (bootstrap.servers) sets a list of host:port
		// pairs for establishing the connections to the Kakfa cluster.

		configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		// Kafka server expects messages in byte[] key, byte[] value
		// format,Rather than converting every key and value,
		// Kafka's client-side library permits us to use types like String and
		// int and The library will convert these to the appropriate type.
		configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");

		// Create a producer Record
		org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);
		String line = in.nextLine();
		while (!line.equals("exit")) {
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, line);
			
			//Send the record
			producer.send(record);
			line = in.nextLine();
		}
		in.close();
		producer.close();
	}
}
