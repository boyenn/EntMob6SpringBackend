/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Temperature;
import be.pxl.backend.models.TemperatureByInterval;
import be.pxl.backend.repositories.TemperatureRepository;
import java.util.ArrayList;
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
 * @author Thijs
 */
@Service
public class TemperatureService {
    //AUTOWIRED PROPERTIES
    @Autowired
    private TemperatureRepository rep;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    public List<Temperature> getAll() {
        return rep.findAll();
    }

    public List<Temperature> getBetweenDates(Date start, Date end) {
        return rep.findByMeasuredBetween(start, end);
    }
    public Temperature findLatest(){
        return rep.findTop1ByOrderByMeasuredDesc();
    }

    public Temperature findOne(String Id) {

        return rep.findOne(Id);
    }

    public Temperature addNew(PostObject<Float> e) {
        Temperature h = new Temperature(e.getValue(), e.getTimeStamp());
        return rep.save(h);
    }

    public Iterable<Temperature> addNew(List<PostObject<Float>> l) {
        ArrayList<Temperature> returnList = new ArrayList<>();
        l.stream().map((e) -> new Temperature(e.getValue(), e.getTimeStamp()))
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
        mongoTemplate.remove(new Query(), "temperaturebyhour");
        mongoTemplate.remove(new Query(), "temperaturebymonth");
        mongoTemplate.remove(new Query(), "temperaturebyday");
        mongoTemplate.remove(new Query(), "temperaturebyminute");
    }
    
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
    public List<TemperatureByInterval> findByIntervalBetween(Date start, Date end, int interval){
        Query query = Query.query(Criteria.where("date").gte(start).lte(end));
      

        
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.find(query, TemperatureByInterval.class, "temperaturebyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.find(query, TemperatureByInterval.class, "temperaturebyhour") ;
            case Calendar.MONTH: return mongoTemplate.find(query, TemperatureByInterval.class, "temperaturebymonth");
            case Calendar.MINUTE:  return mongoTemplate.find(query, TemperatureByInterval.class, "temperaturebyminute");
            default: return null;
        }
        
        
        
    }
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
     public List<TemperatureByInterval> findAllByInterval( int interval){
       
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.findAll( TemperatureByInterval.class, "temperaturebyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.findAll( TemperatureByInterval.class, "temperaturebyhour");
            case Calendar.MONTH: return mongoTemplate.findAll( TemperatureByInterval.class, "temperaturebymonth");
            case Calendar.MINUTE: return mongoTemplate.findAll( TemperatureByInterval.class, "temperaturebyminute"); 
            default: return null;
        }
    }



}

