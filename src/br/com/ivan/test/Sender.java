package br.com.telefonica.test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class Sender {

	private static String host = "localhost";
	private static String port = "61616";
	private static String queue_name = "SERVER.TEST.MESSENGER";
	private static Format formatter = new SimpleDateFormat("HH:mm:ss");
	private static String stime = null;

	public static void main(String[] args) throws Exception {

		//thread(new StartBroker(), true);
		thread(new StartProducer(), false);
	}

	public static void thread(Runnable runnable, boolean daemon) {

		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

	public static class StartBroker implements Runnable {

		public void run() {
			try {
				BrokerService broker = new BrokerService();
				
				
				// configure the broker
				broker.addConnector("tcp://" + host + ":" + port + "");
				broker.start();
			} catch (Exception e) {

				System.out.println("Caught: " + e);
			}
		}
	}

	public static class StartProducer implements Runnable {
		public void run() {
			try {
				
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = null;
				connectionFactory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port + "");

				
				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();
				
				
				// Create a Session
				Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				
				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue(queue_name);
				
				// Create a MessageProducer from the Session
				MessageProducer producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				
				
				// Create a messages
				String msg = "Hello World!!!!";
				Message message = session.createTextMessage(msg);
				Date date = new Date();
				stime = formatter.format(date);
				System.out.println("Mensagem enviada: " + msg + stime);
				
				
				// Tell the producer to send the message
				producer.send(message);
				
				
				// Clean up
				session.close();
				connection.close();
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}
	}
}
