/*
 * Kafka Consumer Program
 * @Shraddha kulkarni
 */
package com.cybage;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class Consumer {
	private static Scanner in;
	private static boolean stop = false;

	// Command Line Arguments : Topic_Name Group_Id
	public static void main(String[] argv) throws Exception {
		if (argv.length != 2) {
			System.err.printf("Provide: <topicName> <groupId>\n");
			System.exit(-1);
		}
		in = new Scanner(System.in);
		String topicName = argv[0];
		String groupId = argv[1];

		ConsumerThread consumerRunnable = new ConsumerThread(topicName, groupId);
		consumerRunnable.start();
		String line = "";
		while (!line.equals("exit")) {
			line = in.next();
		}
		consumerRunnable.getKafkaConsumer().wakeup();
		System.out.println("Stopping consumer .....");
		consumerRunnable.join();
	}

	private static class ConsumerThread extends Thread {
		private String topicName;
		private String groupId;
		private KafkaConsumer<String, String> kafkaConsumer;

		// ConsumerThread is an inner class that takes a topic name and group
		// name as its arguments.

		public ConsumerThread(String topicName, String groupId) {
			this.topicName = topicName;
			this.groupId = groupId;
		}
		// In the run() method we create a KafkaConsumer object, with
		// appropriate properties.

		public void run() {

			Properties configProperties = new Properties();

			configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
			configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringDeserializer");
			configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringDeserializer");
			// value of the GROUP_ID_CONFIG should be a group name in string
			// format
			configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

			// Figure out where to start processing messages from
			kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
			// Kafka Consumer subscribes to the topic that was passed as an
			// argument in the constructor.
			// then polls the Kafka server every 100 milliseconds to check if
			// there are any new messages in the topic.
			kafkaConsumer.subscribe(Arrays.asList(topicName));

			try {
				while (true) {
					ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
					for (ConsumerRecord<String, String> record : records)
						System.out.println(record.value());
				}
			} catch (WakeupException ex) {
				System.out.println("Exception caught " + ex.getMessage());
			} finally {
				kafkaConsumer.close();
				System.out.println("After closing Kafka Consumer");
			}
		}

		public KafkaConsumer<String, String> getKafkaConsumer() {
			return this.kafkaConsumer;
		}
	}
}
