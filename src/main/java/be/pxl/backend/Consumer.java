package be.pxl.backend;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by Boyen on 6/11/2016.
 */
@Component
public class Consumer {


    @JmsListener(destination = "logTopic")
    public void receiveQueue(String text) {
        System.out.println("Recieved : " + text);
    }

}