package be.pxl.backend.monitor;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Account;
import be.pxl.backend.services.AccountService;
import be.pxl.backend.services.HumidityService;
import be.pxl.backend.services.PerformedRequestService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Boyen on 9/11/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration
public class RateLimiterTest {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterTest.class);
    @Autowired
    private AccountService accountService;

    @Autowired
    private PerformedRequestService performedRequestService;

    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${backendratelimiter.maxrequests}")
    private int maxrequests;
    @Before
    @WithMockUser(roles = {"USER","ADMIN"})
    public void setUp(){
        performedRequestService.deleteAll();

    }
    @After
    public void tearDown(){
        performedRequestService.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"USER","ADMIN"})
    public void testRateLimit(){


        for (int i = 0; i < maxrequests; i++) {
            logger.info("forlogger");
            logger.info(String.valueOf(maxrequests));
            logger.info(String.valueOf(i));
            ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/humidity", String.class);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        }
        ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/humidity", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

    }




}