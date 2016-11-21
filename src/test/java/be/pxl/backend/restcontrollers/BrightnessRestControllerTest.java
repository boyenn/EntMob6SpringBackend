/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Brightness;
import be.pxl.backend.models.BrightnessByInterval;
import be.pxl.backend.scheduling.Schedules;
import be.pxl.backend.services.BrightnessService;
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

/**
 *
 * @author Thijs
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BrightnessRestControllerTest {

    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    //AUTOWIRED OBJECTS
    @Autowired
    private TestRestTemplate restTemplate; //Spring's template for testing REST controllers
    @Autowired
    private BrightnessService service;
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

        ResponseEntity<Brightness[]> responseEntity;
        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness", Brightness[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Brightness[] brightnesses = responseEntity.getBody();
        assertEquals("first value : " , 75.1f, brightnesses[0].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals("second value : " ,80f, brightnesses[1].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(2, brightnesses.length);

    }


    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testScenarioPostOneGetList() {
        //Set up date that will be posted
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15); // add 15 minutes
        Date dateToPost = cal.getTime();

        //Check if list is the list from setup
        ResponseEntity<Brightness[]> responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness", Brightness[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Brightness[] brightnesses = responseEntity.getBody();

        assertThat(brightnesses).hasSize(2)
                .extracting(Brightness::getValue).containsOnly(75.1f,80f);

        //Post a new brightness
        ResponseEntity<Brightness> postedEntity = restTemplate.withBasicAuth("boyen","root").postForEntity("/brightness",
                new PostObject<>(95f, dateToPost), Brightness.class);
        Brightness postedBrightness = postedEntity.getBody();
        assertEquals(HttpStatus.CREATED, postedEntity.getStatusCode());
        assertEquals(95f, postedBrightness.getValue(), MAX_ASSERT_FLOAT_OFFSET);

        //Check if list is now changed with the new value
        responseEntity
                = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness", Brightness[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        brightnesses = responseEntity.getBody();

        //Check list length
        assertEquals(3, brightnesses.length);

        //Check brightnesses values
        assertEquals(75.1f, brightnesses[0].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(postedBrightness.getValue(), brightnesses[brightnesses.length-1].getValue(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(95f, brightnesses[brightnesses.length-1].getValue(), MAX_ASSERT_FLOAT_OFFSET);

        //Check brightness dates
        assertEquals(postedBrightness.getValue(), brightnesses[brightnesses.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);
        assertEquals(dateToPost.getTime(), brightnesses[brightnesses.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);



    }
    @Test
    @WithMockUser(username = "boyen",password = "root",roles = {"USER","ADMIN"})
    public void testLatest(){
        ResponseEntity<Brightness> responseEntity;
        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/latest", Brightness.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Brightness brightness = responseEntity.getBody();
        assertEquals( 80f, brightness.getValue(), MAX_ASSERT_FLOAT_OFFSET);

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
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/month?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1, brightnesses.length );
        assertEquals((75.1f+80f)/2f, brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);



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

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightnesses/month?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightnesses/month?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/month?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();
        assertEquals(0,brightnesses.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMonthWithoutParam() {

        ResponseEntity<BrightnessByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/month", BrightnessByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1,brightnesses.length );
        assertEquals((75.1f+80f)/2f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);

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
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/day?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1,brightnesses.length);
        assertEquals((75.1f+80f)/2f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);



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

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/day?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/day?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/day?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();
        assertEquals(0,brightnesses.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListDayWithoutParam() {

        ResponseEntity<BrightnessByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/day", BrightnessByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1,brightnesses.length );
        assertEquals((75.1f+80f)/2f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);

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
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/hour?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1,brightnesses.length );
        assertEquals((75.1f+80f)/2f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);



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

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/hour?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/hour?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/hour?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();
        assertEquals(0,brightnesses.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListHourWithoutParam() {

        ResponseEntity<BrightnessByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/hour", BrightnessByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(1,brightnesses.length );
        assertEquals((75.1f+80f)/2f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);

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
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/minute?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(2,brightnesses.length );
        assertEquals(80f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(75.1f,brightnesses[1].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);


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

        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/minute?start={start}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        responseEntity = restTemplate.withBasicAuth("boyen","root").exchange("/brightness/minute?end={end}", HttpMethod.GET, HttpEntity.EMPTY, String.class,start);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());



    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithNonInclusiveParam() {
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        Date end = c.getTime();
        ResponseEntity<BrightnessByInterval[]> responseEntity;
        HashMap<String,String> urlParams = new HashMap<>();
        urlParams.put("start", new DateTime(start).toString(ISODateTimeFormat.dateTime()));
        urlParams.put("end",new DateTime(end).toString(ISODateTimeFormat.dateTime()));

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/minute?start={start}&end={end}", BrightnessByInterval[].class,urlParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();
        assertEquals(0,brightnesses.length );

    }
    @Test
    @WithMockUser(username="boyen",password = "root",roles = {"USER","ADMIN"})
    public void testIntervalListMinuteWithoutParam() {

        ResponseEntity<BrightnessByInterval[]> responseEntity;

        responseEntity = restTemplate.withBasicAuth("boyen","root").getForEntity("/brightness/minute", BrightnessByInterval[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BrightnessByInterval[] brightnesses = responseEntity.getBody();

        assertEquals(2,brightnesses.length );
        assertEquals(80f,brightnesses[0].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(75.1f,brightnesses[1].getAvVal(),MAX_ASSERT_FLOAT_OFFSET);
    }







}
