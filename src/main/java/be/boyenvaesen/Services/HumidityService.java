/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Services;

import be.boyenvaesen.Models.*;
import be.boyenvaesen.Repositories.HumidityByHourRepository;
import be.boyenvaesen.Repositories.HumidityByIntervalRepository;
import be.boyenvaesen.Repositories.HumidityByMinuteRepository;
import be.boyenvaesen.Repositories.HumidityRepository;
import be.boyenvaesen.helpers.postObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Boyen
 */
@Service
public class HumidityService {

    @Autowired
    private HumidityRepository rep;

   
    @Autowired
    private HumidityByMinuteRepository minuteRep;
    @Autowired
    private HumidityByHourRepository hourRep;
     @Autowired
    private HumidityByIntervalRepository intervalRep;
    public List<Humidity> getAll() {
        return (List<Humidity>) rep.findAll();
    }
    public List<Humidity> getBetweenDates(Date start, Date end){
        return rep.findByMeasuredBetween(start, end);
    }



    public Humidity findOne(long Id) {

        return rep.findOne(Id);
    }

    public Humidity addNew(postObject<Float> e) {
        Humidity h = new Humidity(e.getValue(), e.getTimeStamp());
        return rep.save(h);
    }

    public Iterable<Humidity> addNew(List<postObject<Float>> l) {
        ArrayList<Humidity> returnList = new ArrayList<>();
        l.stream().map((e) -> new Humidity(e.getValue(), e.getTimeStamp()))
                .forEachOrdered((h) -> { //garandeerd de juiste volgorde
                    returnList.add(rep.save(h));
                });
        return returnList;
    }

    

    public void deleteAll() {
        rep.deleteAll();
    }
    
    
    //BY INTERVAL
   public <T extends HumidityByInterval> List<T>  getByInterval(Class <T> type){
        if(type == HumidityByMinute.class){
           return (List<T>)minuteRep.findAll();
       }
        else if(type == HumidityByHour.class){
            return (List<T>)hourRep.findAll();
       }
        return null;
   }
    public <T extends HumidityByInterval> List<T>  getBetweenDatesByInterval(Class <T> type,Date start,Date end){
       if(type == HumidityByMinute.class){
           return (List<T>)minuteRep.findByAtTimeBetween(start, end);
       }
       else if(type == HumidityByHour.class){
            return (List<T>)hourRep.findByAtTimeBetween(start, end);
       }
       return null;
   }
    
   public void saveByInterval(HumidityByInterval hbm){
       intervalRep.save(hbm);
   }
    
   
}
