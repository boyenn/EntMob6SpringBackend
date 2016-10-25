/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.scheduling;

import be.boyenvaesen.Models.Humidity;
import be.boyenvaesen.Models.HumidityByHour;
import be.boyenvaesen.Models.HumidityByInterval;
import be.boyenvaesen.Models.HumidityByMinute;
import be.boyenvaesen.Services.HumidityService;
import be.boyenvaesen.helpers.postObject;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author Boyen
 */
@Component
public class HumiditySchedules {

    private static final Logger log = LoggerFactory.getLogger(HumiditySchedules.class);
    @Autowired
    public HumidityService service;

    @Scheduled(fixedRate = 2000)
    public void updateIntervalDatabases() {
        log.info("Starting database cleanup");
        
        Calendar c = Calendar.getInstance();
        Calendar calForCalculations = Calendar.getInstance();
        //Clean records dating till :
        c.add(Calendar.HOUR, -5);
        Date now = new Date();
        
        List<Humidity> allHumidities = service.getBetweenDates(c.getTime(), now);
        List<HumidityByMinute> humiditiesByMinute = service.getBetweenDatesByInterval(HumidityByMinute.class,c.getTime(),now);
        List<HumidityByHour> humidityByHour = service.getBetweenDatesByInterval(HumidityByHour.class,c.getTime(),now);
        
        //Group humidities by minute
        Map<Date, List<Humidity>> map = allHumidities.stream().collect(Collectors.groupingBy((t) -> {

            calForCalculations.setTime(t.getMeasured());
            calForCalculations.set(Calendar.SECOND, 0);
            calForCalculations.set(Calendar.MILLISECOND, 0);

            return calForCalculations.getTime();
        }));
        //CALCULATE MINUTES
        map.forEach((t, u) -> {
            HumidityByMinute toCalc = humiditiesByMinute.stream().filter((q) -> {
                return q.getAtTime().compareTo(t) == 0;
            }).findAny().orElse(new HumidityByMinute(t));
            float average = (float) u
                    .stream()
                    .mapToDouble(Humidity::getPercentage)
                    .average()
                    .getAsDouble();
            toCalc.setAveragePercentage(average);
            service.saveByInterval(toCalc);
        });

        List<HumidityByMinute> newByMinute = service.getBetweenDatesByInterval(HumidityByMinute.class,c.getTime(),now);
        //Group HumidityByMinute By hour
        Map<Date, List<HumidityByMinute>> mapByHour = newByMinute.stream().collect(Collectors.groupingBy((t) -> {
            calForCalculations.setTime(t.getAtTime());
            calForCalculations.set(Calendar.MINUTE, 0);
            return calForCalculations.getTime();
        }));
        //CALCULATE HOURS
        mapByHour.forEach((t, u) -> {
           
           HumidityByHour toCalc = humidityByHour.stream().filter((q) -> {
               return q.getAtTime().compareTo(t) == 0;
           }).findAny().orElse(new HumidityByHour(t));
            float average = (float) u
                    .stream()
                    .mapToDouble(HumidityByMinute::getAveragePercentage)
                    .average()
                    .getAsDouble();
            toCalc.setAveragePercentage(average);
            service.saveByInterval(toCalc);

        });
        log.info("finished cleaning database");

    }
}
