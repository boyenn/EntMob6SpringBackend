/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Humidity;
import be.pxl.backend.models.HumidityByInterval;
import be.pxl.backend.repositories.HumidityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Boyen
 */
@Service
public class HumidityService {
    //AUTOWIRED PROPERTIES
    @Autowired
    private HumidityRepository rep;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    public List<Humidity> getAll() {
        return rep.findAll();
    }

    public List<Humidity> getBetweenDates(Date start, Date end) {
        return rep.findByMeasuredBetween(start, end);
    }
    public Humidity findLatest(){
        return rep.findTop1ByOrderByMeasuredDesc();
    }

    public Humidity findOne(String Id) {

        return rep.findOne(Id);
    }

    public Humidity addNew(PostObject<Float> e) {
        Humidity h = new Humidity(e.getValue(), e.getTimeStamp());
        return rep.save(h);
    }

    public Iterable<Humidity> addNew(List<PostObject<Float>> l) {
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
    public void deleteAllByInterval(){
        mongoTemplate.remove(new Query(), "humiditybyhour");
        mongoTemplate.remove(new Query(), "humiditybymonth");
        mongoTemplate.remove(new Query(), "humiditybyday");
        mongoTemplate.remove(new Query(), "humiditybyminute");
    }
    
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
    public List<HumidityByInterval> findByIntervalBetween(Date start, Date end, int interval){
        Query query = Query.query(Criteria.where("date").gte(start).lte(end));
      

        
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyhour") ;
            case Calendar.MONTH: return mongoTemplate.find(query, HumidityByInterval.class, "humiditybymonth");
            case Calendar.MINUTE:  return mongoTemplate.find(query, HumidityByInterval.class, "humiditybyminute");
            default: return null;
        }
        
        
        
    }
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
     public List<HumidityByInterval> findAllByInterval( int interval){
       
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.findAll( HumidityByInterval.class, "humiditybyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybyhour");
            case Calendar.MONTH: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybymonth");
            case Calendar.MINUTE: return mongoTemplate.findAll( HumidityByInterval.class, "humiditybyminute"); 
            default: return null;
        }
    }



}
