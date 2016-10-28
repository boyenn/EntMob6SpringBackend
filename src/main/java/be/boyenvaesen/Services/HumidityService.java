/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Services;

import be.boyenvaesen.Models.*;
import be.boyenvaesen.Repositories.HumidityRepository;
import be.boyenvaesen.helpers.postObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private MongoTemplate mongoTemplate;

    public List<Humidity> getAll() {
        return (List<Humidity>) rep.findAll();
    }

    public List<Humidity> getBetweenDates(Date start, Date end) {
        return rep.findByMeasuredBetween(start, end);
    }

    public Humidity findOne(String Id) {

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
    
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
    public List<HumidityByInterval> findByIntervalBetween(Date start,Date end, int interval){
        Query query = Query.query(Criteria.where("data").gte(start).lte(end));
      
        if(interval==Calendar.HOUR){
            
           return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyhour");
        }
        
        switch(interval){
            case Calendar.HOUR: return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyhour"); 
            case Calendar.MONTH: return mongoTemplate.find(query, HumidityByInterval.class, "humiditybymonth");
            case Calendar.MINUTE:  return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyminute"); 
            default: return null;
        }
        
        
        
    }
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
     public List<HumidityByInterval> findAllByInterval( int interval){
       
        switch(interval){
            case Calendar.HOUR: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybyhour");
            case Calendar.MONTH: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybymonth");
            case Calendar.MINUTE: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybyminute"); 
            default: return null;
        }
    }

}
