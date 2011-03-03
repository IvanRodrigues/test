package br.com.telefonica.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {

	private static String host = "localhost";
	private static String port = "61616";
	private static String queue_name = "SERVER.TEST.MESSENGER";

	public static void main(String[] args) throws Exception {
		while (true) {
			thread(new StartConsumer(), false);
			Thread.sleep(5000);
		}
	}

	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

	public static class StartConsumer implements Runnable, ExceptionListener {
		public void run() {
			try {
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port + "");

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();
				connection.setExceptionListener(this);

				// Create a Session
				Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue(queue_name);

				// Create a MessageConsumer from the Session to the
				MessageConsumer consumer = session.createConsumer(destination);

				// Wait for a message
				Message message = consumer.receive(1000);

				onMessage(message);

				consumer.close();
				session.close();
				connection.close();

			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}

		public synchronized void onException(JMSException ex) {
			System.out.println("JMS Exception occured. Shutting down client.");
		}
	}

	public static void onMessage(javax.jms.Message jmsMessage) {
		try {
			if (jmsMessage instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) jmsMessage;
				String text = textMessage.getText();
				System.out.println("Received: -" + text);
				if (text.contains("address")) {
					System.out.println(">>> Sending...");

				}
			}
		} catch (javax.jms.JMSException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
