/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Humidity;

import be.boyenvaesen.Services.HumidityService;
import be.boyenvaesen.helpers.postObject;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Boyen
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HumidityServiceyByDateTest {
    
    
    /* Test service by defining precision */
    @Autowired
    private HumidityService service; 
      
        @Before
        public void setUp() {

        service.deleteAll();
        Calendar c = Calendar.getInstance();
        c.set(2016, 10, 23, 14, 00);
        for(int i=0;i<10;i++){
            
            service.addNew(new postObject<>(50f+5f*i,c.getTime()));
            c.add(Calendar.MINUTE, 10);
        }
        
    }
}
