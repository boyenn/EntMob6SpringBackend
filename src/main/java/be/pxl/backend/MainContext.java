package be.pxl.backend;

import be.pxl.backend.models.Account;
import be.pxl.backend.repositories.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@Configuration
@EnableJms
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableMongoAuditing
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


}
