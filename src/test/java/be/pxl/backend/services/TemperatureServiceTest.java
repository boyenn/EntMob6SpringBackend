/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Temperature;
import be.pxl.backend.models.TemperatureByInterval;
import be.pxl.backend.repositories.TemperatureByIntervalRepository;
import be.pxl.backend.repositories.TemperatureRepository;

import be.pxl.backend.scheduling.Schedules;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Thijs
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TemperatureServiceTest {
    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final double MAX_ASSERT_DOUBLE_OFFSET = 0.001d; //Maximum difference for a double to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    @Autowired
    private TemperatureRepository temperatureRepository;
    @Autowired
    private TemperatureByIntervalRepository temperatureByIntervalRepository;
    @Autowired
    private TemperatureService temperatureService;
    @Autowired
    private Schedules schedules;
    private Temperature temperature1;
    private Temperature temperature2;
    private Date firstDate;
    private Date secondDate;
    @Before
    public void setUp(){

        temperatureByIntervalRepository.deleteAll();
        temperatureRepository.deleteAll();
        temperatureService.deleteAllByInterval();

        Calendar cal = Calendar.getInstance();

        firstDate =cal.getTime();

        cal.add(Calendar.MINUTE, 10); // add 10 minutes
        secondDate = cal.getTime();

        temperature1=  new Temperature(75.1f, firstDate);
        temperature2=  new Temperature(80f, secondDate);
        temperature1=temperatureRepository.save(
               temperature1
                );
        temperature2=temperatureRepository.save(
                temperature2
        );
        schedules.updateIntervalDatabases();


    }
    @After
    public void tearDown(){
        temperatureRepository.deleteAll();

    }
    @Test
    public void getAll() throws Exception {
        assertThat(temperatureService.getAll()).extracting("value").containsOnly(75.1f,80f);
        assertThat(temperatureService.getAll()).extracting("measured").extracting("time").containsOnly(firstDate.getTime(),secondDate.getTime());

    }

    @Test
    public void getBetweenDates() throws Exception {
        long firstDateOriginalTime = firstDate.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDate);
        cal.add(Calendar.SECOND,-1);
        firstDate =cal.getTime();
        cal.setTime(secondDate);
        cal.add(Calendar.SECOND,-1);
        secondDate=cal.getTime();
        assertThat(temperatureService.getBetweenDates(firstDate,secondDate)).extracting("value").containsOnly(75.1f);
        assertThat(temperatureService.getBetweenDates(firstDate,secondDate)).extracting("measured").extracting("time").containsOnly(firstDateOriginalTime);
    }

    @Test
    public void addNew() throws Exception {
        Date date = new Date();
        Temperature hm = temperatureService.addNew(new PostObject<Float>(42f,date));
        assertThat(hm.getMeasured()).hasSameTimeAs(date);
        assertThat(hm.getValue()).isEqualTo(42f,offset(MAX_ASSERT_FLOAT_OFFSET));
    }


    @Test
    public void findByIntervalBetween() throws Exception {

    }
    @Test
    public void findAllByIntervalMinute() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);
        List<TemperatureByInterval> a = temperatureService.findAllByInterval(Calendar.MINUTE);
        a=a.stream().sorted(Comparator.comparing(TemperatureByInterval::getDate)).collect(Collectors.toList());
        assertThat(a.size()).isEqualTo(2);
        assertThat(
                a.get(a.size()-2).getDate())
                .hasMinute(c.get(Calendar.MINUTE));
        assertThat(a.get(a.size()-2).getDate()).hasSecond(0);

        c.setTime(secondDate);

        assertThat(
                a.get(a.size()-1).getDate())
                .hasMinute(c.get(Calendar.MINUTE));
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }
    @Test
    public void findAllByIntervalHour() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);

        List<TemperatureByInterval> a = temperatureService.findAllByInterval(Calendar.HOUR_OF_DAY);
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(c.get(Calendar.HOUR_OF_DAY));
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }
    @Test
    public void findAllByIntervalDay() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);

        List<TemperatureByInterval> a = temperatureService.findAllByInterval(Calendar.DAY_OF_MONTH);
        assertThat(a.get(a.size()-1).getDate()).hasDayOfMonth(c.get(Calendar.DAY_OF_MONTH));
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(1);
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }
    @Test
    public void findAllByIntervalMonth() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);

        List<TemperatureByInterval> a = temperatureService.findAllByInterval(Calendar.MONTH);
        assertThat(a.get(a.size()-1).getDate()).hasMonth(c.get(Calendar.MONTH)+1);
        assertThat(a.get(a.size()-1).getDate()).hasDayOfMonth(1);
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(1);
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }

}