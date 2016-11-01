package be.boyenvaesen;

import be.boyenvaesen.Models.Account;
import be.boyenvaesen.Repositories.AccountRepository;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@Configuration
public class MainContext {

    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(MainContext.class, args);
    }

    @Bean
    CommandLineRunner init(final AccountRepository accountRepository) {

        return new CommandLineRunner() {

            @Override
            public void run(String... arg0) throws Exception {
                List<String> roles = Arrays.asList("USER","ADMIN");
                accountRepository.save(new Account("boyen", "root",true,roles));

            }

        };

    }
}
