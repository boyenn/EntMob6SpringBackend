/*
package be.pxl.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.Topic;

*/
/**
 * Created by Boyen on 6/11/2016.
 *//*

@Component
public class Producer implements CommandLineRunner {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Topic topic;

    @Override
    public void run(String... args) throws Exception {
        send("Sample message");
        System.out.println("Message was sent to the Queue");
    }
    @Scheduled(fixedRate = 500)
    public void scheduledSend(){
        send("Sample message");
    }

    public void send(String msg) {
        this.jmsMessagingTemplate.convertAndSend(this.topic, msg);
    }

}
*/
