package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Humidity;
import be.pxl.backend.models.HumidityByInterval;
import be.pxl.backend.repositories.HumidityByIntervalRepository;
import be.pxl.backend.repositories.HumidityRepository;
import be.pxl.backend.scheduling.HumiditySchedules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Created by Boyen on 7/11/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HumidityServiceTest {
    //CONSTANTS
    private final float MAX_ASSERT_FLOAT_OFFSET = 0.001f; //Maximum difference for a float to be considered equal in our case
    private final double MAX_ASSERT_DOUBLE_OFFSET = 0.001d; //Maximum difference for a double to be considered equal in our case
    private final long MAX_ASSERT_DATE_MILLISECONDS_OFFSET = 1000L;
    @Autowired
    private HumidityRepository humidityRepository;
    @Autowired
    private HumidityByIntervalRepository humidityByIntervalRepository;
    @Autowired
    private HumidityService humidityService;
    @Autowired
    HumiditySchedules humiditySchedules;
    private Humidity humidity1;
    private Humidity humidity2;
    private Date firstDate;
    private Date secondDate;
    @Before
    public void setUp(){

        humidityByIntervalRepository.deleteAll();
        humidityRepository.deleteAll();
        humidityService.deleteAllByInterval();

        Calendar cal = Calendar.getInstance();

        firstDate =cal.getTime();

        cal.add(Calendar.MINUTE, 10); // add 10 minutes
        secondDate = cal.getTime();

        humidity1=  new Humidity(75.1f, firstDate);
        humidity2=  new Humidity(80f, secondDate);
        humidity1=humidityRepository.save(
               humidity1
                );
        humidity2=humidityRepository.save(
                humidity2
        );
        humiditySchedules.updateIntervalDatabases();


    }
    @After
    public void tearDown(){
        humidityRepository.deleteAll();

    }
    @Test
    public void getAll() throws Exception {
        assertThat(humidityService.getAll()).extracting("percentage").containsOnly(75.1f,80f);
        assertThat(humidityService.getAll()).extracting("measured").extracting("time").containsOnly(firstDate.getTime(),secondDate.getTime());

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
        assertThat(humidityService.getBetweenDates(firstDate,secondDate)).extracting("percentage").containsOnly(75.1f);
        assertThat(humidityService.getBetweenDates(firstDate,secondDate)).extracting("measured").extracting("time").containsOnly(firstDateOriginalTime);
    }

    @Test
    public void addNew() throws Exception {
        Date date = new Date();
        Humidity hm = humidityService.addNew(new PostObject<Float>(42f,date));
        assertThat(hm.getMeasured()).hasSameTimeAs(date);
        assertThat(hm.getPercentage()).isEqualTo(42f,offset(MAX_ASSERT_FLOAT_OFFSET));
    }


    @Test
    public void findByIntervalBetween() throws Exception {

    }
    @Test
    public void findAllByIntervalMinute() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);
        List<HumidityByInterval> a = humidityService.findAllByInterval(Calendar.MINUTE);
        a=a.stream().sorted(Comparator.comparing(HumidityByInterval::getDate)).collect(Collectors.toList());
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

        List<HumidityByInterval> a = humidityService.findAllByInterval(Calendar.HOUR);
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(c.get(Calendar.HOUR_OF_DAY));
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }
    @Test
    public void findAllByIntervalDay() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);

        List<HumidityByInterval> a = humidityService.findAllByInterval(Calendar.DAY_OF_MONTH);
        assertThat(a.get(a.size()-1).getDate()).hasDayOfMonth(c.get(Calendar.DAY_OF_MONTH));
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(1);
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }
    @Test
    public void findAllByIntervalMonth() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(firstDate);

        List<HumidityByInterval> a = humidityService.findAllByInterval(Calendar.MONTH);
        assertThat(a.get(a.size()-1).getDate()).hasMonth(c.get(Calendar.MONTH+1));
        assertThat(a.get(a.size()-1).getDate()).hasDayOfMonth(0);
        assertThat(a.get(a.size()-1).getDate()).hasHourOfDay(1);
        assertThat(a.get(a.size()-1).getDate()).hasMinute(0);
        assertThat(a.get(a.size()-1).getDate()).hasSecond(0);
    }

}