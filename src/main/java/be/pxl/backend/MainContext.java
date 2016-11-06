package be.pxl.backend;

import be.pxl.backend.models.Account;
import be.pxl.backend.repositories.AccountRepository;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@Configuration
@EnableJms
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class MainContext {


    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(MainContext.class, args);
    }

    @Bean
    CommandLineRunner init(final AccountRepository accountRepository) {

        return arg0 -> {
            List<String> roles = Arrays.asList("USER","ADMIN");
            accountRepository.save(new Account("boyen", "root",true,roles));

        };

    }
    /*@Bean
    Topic topic (){
        return new ActiveMQTopic("beanTopc");
    }*/

}
