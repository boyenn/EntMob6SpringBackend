/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

/**
 *
 * @author Thijs
 */

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.AirPressure;
import be.pxl.backend.models.AirPressureByInterval;

import be.pxl.backend.scheduling.Schedules;
import be.pxl.backend.services.AirPressureService;
import be.pxl.backend.services.PerformedRequestService;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AirPressureRestControllerTest {

    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    //AUTOWIRED OBJECTS
    @Autowired
    private TestRestTemplate restTemplate; //Spring's template for testing REST controllers
    @Autowired
    private AirPressureService service;
    @Autowired
    private PerformedRequestService performedRequestService;

    //TEST OBJECTS
    private Date firstDate;
    private Date secondDate;
    @Autowired
    private Schedules schedules;
    @Before
    public void setUp() {
        service.deleteAllByInterval();
        performedRequestService.deleteAll();
        service.deleteAll();
        firstDate = new Date();
        Calendar cal = Calendar.getInstance();
       
        cal.setTime(firstDate);
        cal.add(Calendar.MINUTE, 10); // add 10 minutes
        secondDate = cal.getTime();

        service.addNew(Arrays.asList(
                new PostObject<>(75.1f, firstDate),
                new PostObject<>(80f, secondDate)));
        schedules.updateIntervalDatabases();
    }

    @After
    public void tearDown(){
        performedRequestService.deleteAll();
    }

    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testList() {

        ResponseEntity<AirPressure[]> responseEntity;
        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure", AirPressure[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressure[] airpressures = responseEntity.getBody();
        assertEquals("first value : " , 75.1f, airpressures[0].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals("second value : " ,80f, airpressures[1].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(2, airpressures.length);

    }


    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testScenarioPostOneGetList() {
        //Set up date that will be posted
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15); // add 15 minutes
        Date dateToPost = cal.getTime();

        //Check if list is the list from setup
        ResponseEntity<AirPressure[]> responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure", AirPressure[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressure[] airPressures = responseEntity.getBody();

        assertThat(airPressures).hasSize(2)
                .extracting(AirPressure::getValue).containsOnly(75.1f,80f);

        //Post a new airpressure
        ResponseEntity<AirPressure> postedEntity = restTemplate.withBasicAuth("boyen","root").postForEntity("/airpressure",
                new PostObject<>(95f, dateToPost), AirPressure.class);
        AirPressure postedAirPressure = postedEntity.getBody();
        assertEquals(HttpStatus.CREATED, postedEntity.getStatusCode());
        assertEquals(95f, postedAirPressure.getValue(), MAX_ASSERT_FLOAT_OFFSET);

        //Check if list is now changed with the new value
        responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure", AirPressure[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        airPressures = responseEntity.getBody();

        //Check list length
        assertEquals(3, airPressures.length);

        //Check airPressure values
        assertEquals(75.1f, airPressures[0].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(postedAirPressure.getValue(), airPressures[airPressures.length-1].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(95f, airPressures[airPressures.length-1].getValue(), MAX_ASSERT_FLOAT_OFFSET);

        //Check airPressure dates
        assertEquals(postedAirPressure.getValue(), airPressures[airPressures.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);
        assertEquals(dateToPost.getTime(), airPressures[airPressures.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);



    }
    @Test
    @WithMockUser(username = "boyen",password = "root",roles = {"USER","ADMIN"})
    public void testLatest(){
        ResponseEntity<AirPressure> responseEntity;
        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/latest", AirPressure.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressure airPressure = responseEntity.getBody();
        assertEquals( 80f, airPressure.getValue(), MAX_ASSERT_FLOAT_OFFSET);

    }


    //MONTH
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithParam() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);
        Date start = c.getTime();
        c.add(Calendar.MONTH,+2);
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/month?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length );
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithWrongParamAmount() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);
        Date start = c.getTime();
        c.add(Calendar.MONTH,+2);
        Date end = c.getTime();
        ResponseEntity<String> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/month?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/month?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/month?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();
        assertEquals(0,airPressures.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithoutParam() {

        ResponseEntity<AirPressureByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/month", AirPressureByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length );
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);

    }
    //DAY
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithParam() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1);
        Date start = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,+2);
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/day?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length);
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithWrongParamAmount() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1);
        Date start = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,+2);
        Date end = c.getTime();
        ResponseEntity<String> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/day?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/day?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/day?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();
        assertEquals(0,airPressures.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithoutParam() {

        ResponseEntity<AirPressureByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/day", AirPressureByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length );
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);

    }

    //HOUR
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithParam() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY,-1);
        Date start = c.getTime();
        c.add(Calendar.HOUR_OF_DAY,+2);
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/hour?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length );
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithWrongParamAmount() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY,-1);
        Date start = c.getTime();
        c.add(Calendar.HOUR_OF_DAY,+2);
        Date end = c.getTime();
        ResponseEntity<String> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/hour?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/hour?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/hour?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();
        assertEquals(0,airPressures.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithoutParam() {

        ResponseEntity<AirPressureByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/hour", AirPressureByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(1,airPressures.length );
        assertEquals((75.1f+80f)/2f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);

    }
    //MINUTE
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithParam() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY,-1);
        Date start = c.getTime();
        c.add(Calendar.HOUR_OF_DAY,+2);
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/minute?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(2,airPressures.length );
        assertEquals(80f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(75.1f,airPressures[1].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);


    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithWrongParamAmount() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY,-1);
        Date start = c.getTime();
        c.add(Calendar.HOUR_OF_DAY,+2);
        Date end = c.getTime();
        ResponseEntity<String> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/minute?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/airpressure/minute?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<AirPressureByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/minute?start={start}&end={end}", AirPressureByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();
        assertEquals(0,airPressures.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithoutParam() {

        ResponseEntity<AirPressureByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/airpressure/minute", AirPressureByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AirPressureByInterval[] airPressures = responseEntity.getBody();

        assertEquals(2,airPressures.length );
        assertEquals(80f,airPressures[0].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(75.1f,airPressures[1].getAvPer(),MAX_ASSERT_FLOAT_OFFSET);
    }







}
