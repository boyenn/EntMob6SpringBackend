/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Humidity;
import be.pxl.backend.services.HumidityService;
import be.pxl.backend.services.PerformedRequestService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

public class HumidityRestControllerTest {

    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    //AUTOWIRED OBJECTS
    @Autowired
    private TestRestTemplate restTemplate; //Spring's template for testing REST controllers
    @Autowired
    private HumidityService service;
    @Autowired
    private PerformedRequestService performedRequestService;

    //TEST OBJECTS
    private Date firstDate;
    private Date secondDate;

    @Before
    public void setUp() {
        performedRequestService.deleteAll();
        service.deleteAll();
        firstDate = new Date();
        Calendar cal = Calendar.getInstance();
       
        cal.setTime(firstDate);
        cal.add(Calendar.MINUTE, 10); // add 10 minutes
        secondDate = cal.getTime();
        
        System.out.println(cal.getTime());

        service.addNew(Arrays.asList(
                new PostObject<>(75.1f, firstDate),
                new PostObject<>(80f, secondDate)));

    }

    @After
    public void tearDown(){
        performedRequestService.deleteAll();
    }

    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testList() {

        ResponseEntity<Humidity[]> responseEntity;
        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Humidity[] humidities = responseEntity.getBody();
        assertEquals("first percentage : " , 75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals("second percentage : " ,80f, humidities[1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(2, humidities.length);

    }

    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testScenarioPostOneGetList() {
        //Set up date that will be posted
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 15); // add 15 minutes
        Date dateToPost = cal.getTime();

        //Check if list is the list from setup
        ResponseEntity<Humidity[]> responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Humidity[] humidities = responseEntity.getBody();

        assertEquals(75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(80f, humidities[1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(2, humidities.length);

        //Post a new restcontrollers
        ResponseEntity<Humidity> postedEntity = restTemplate.withBasicAuth("boyen","root").postForEntity("/humidity",
                new PostObject<>(95f, dateToPost), Humidity.class);
        Humidity postedHumidity = postedEntity.getBody();
        assertEquals(HttpStatus.CREATED, postedEntity.getStatusCode());
        assertEquals(95f, postedHumidity.getPercentage(), MAX_ASSERT_FLOAT_OFFSET);

        //Check if list is now changed with the new value
        responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        humidities = responseEntity.getBody();

        //Check humidity percentages
        assertThat(humidities).hasSize(3).contains(humidities[0],humidities[1]);
        assertEquals(75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(postedHumidity.getPercentage(), humidities[humidities.length-1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(95f, humidities[humidities.length-1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);

        //Check humidity dates
        assertEquals(postedHumidity.getMeasured().getTime(), humidities[humidities.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);
        assertEquals(dateToPost.getTime(), humidities[humidities.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);

        //Check list length
        assertEquals(3, humidities.length);

    }




    
//    @Test
//    public void test

}
