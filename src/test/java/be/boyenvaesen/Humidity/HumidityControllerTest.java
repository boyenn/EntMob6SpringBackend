/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Humidity;

import be.boyenvaesen.Models.*;
import be.boyenvaesen.Services.*;
import be.boyenvaesen.helpers.postObject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HumidityControllerTest {

    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    //AUTOWIRED OBJECTS
    @Autowired
    private TestRestTemplate restTemplate; //Spring's template for testing REST controllers
    @Autowired
    private HumidityService service; 

    //TEST OBJECTS
    private Date firstDate;
    private Date secondDate;

    @Before
    public void setUp() {
        Random r = new Random();
        service.deleteAll();
        firstDate = new Date();
        Calendar cal = Calendar.getInstance();
       
        cal.setTime(firstDate);
        cal.add(Calendar.MINUTE, 10); // add 10 minutes
        secondDate = cal.getTime();
        
        System.out.println(cal.getTime());
        service.addNew(Arrays.asList(
                new postObject<>(75.1f, firstDate),
                new postObject<>(80f, secondDate)));
//        for(int i=0;i<200;i++){
//             cal.add(Calendar.MINUTE, r.nextInt(10));
//            service.addNew(new postObject<>((float)r.nextInt(100),cal.getTime()));
//        }
    }

//    @Test
//    public void testSingle() {
//
//        ResponseEntity<Employee> responseEntity
//                = restTemplate.getForEntity("/employees/" + singleEmployee.getId(), Employee.class);
//        Employee employee = responseEntity.getBody();
//        Assert.assertEquals("Boyen", employee.getFirstName());
//    }
    @Test
    public void testList() {

        ResponseEntity<Humidity[]> responseEntity
                = restTemplate.getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Humidity[] humidities = responseEntity.getBody();
        assertEquals("first percentage : " , 75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals("second percentage : " ,80f, humidities[1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        //assertEquals(2, humidities.length);

    }

    @Test
    public void testScenarioPostOneGetList() {
        //Set up date that will be posted
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 15); // add 10 minutes
        Date postedDate = cal.getTime();

        //Check if list is the list from setup
        ResponseEntity<Humidity[]> responseEntity
                = restTemplate.getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Humidity[] humidities = responseEntity.getBody();

        assertEquals(75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(80f, humidities[1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
//        assertEquals(2, humidities.length);

        //Post a new Humidity
        ResponseEntity<Humidity> postedEntity = restTemplate.postForEntity("/humidity", 
                new postObject<>(95f, postedDate), Humidity.class);
        Humidity postedHumidity = postedEntity.getBody();
        assertEquals(HttpStatus.CREATED, postedEntity.getStatusCode());
        assertEquals(95f, postedHumidity.getPercentage(), MAX_ASSERT_FLOAT_OFFSET);

        //Check if list is now changed with the new value
        responseEntity
                = restTemplate.getForEntity("/humidity", Humidity[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        humidities = responseEntity.getBody();
        //Check humidity percentages
       // assertThat(humidities).hasSize(3).contains(humidities[0],humidities[1]);
        
        assertEquals(75.1f, humidities[0].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(postedHumidity.getPercentage(), humidities[humidities.length-1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        assertEquals(95f, humidities[humidities.length-1].getPercentage(), MAX_ASSERT_FLOAT_OFFSET);
        //Check humidity dates
        assertEquals(postedHumidity.getMeasured().getTime(), humidities[humidities.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);
        assertEquals(postedDate.getTime(), humidities[humidities.length-1].getMeasured().getTime(),MAX_ASSERT_DATE_MILLISECONDS_OFFSET);
        //Check list length
        //assertEquals(3, humidities.length);

    }
    
  
    
//    @Test
//    public void test

}
