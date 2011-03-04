package br.com.telefonica.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.telefonica.test.Receiver.StartConsumer;

public class SendAndReceiver {

	
	@Autowired
	Receiver receiver;
	
	public static void main(String[] args) {
		new SendAndReceiver();
	}
	

	public SendAndReceiver() {
		
		while (true) {

			receiver.thread(new StartConsumer(), false);
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
