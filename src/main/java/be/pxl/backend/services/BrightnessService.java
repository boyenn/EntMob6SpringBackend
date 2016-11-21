/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Brightness;
import be.pxl.backend.models.BrightnessByInterval;
import be.pxl.backend.repositories.BrightnessRepository;

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
public class BrightnessService {
    //AUTOWIRED PROPERTIES
    @Autowired
    private BrightnessRepository rep;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    public List<Brightness> getAll() {
        return rep.findAll();
    }

    public List<Brightness> getBetweenDates(Date start, Date end) {
        return rep.findByMeasuredBetween(start, end);
    }
    public Brightness findLatest(){
        return rep.findTop1ByOrderByMeasuredDesc();
    }

    public Brightness findOne(String Id) {

        return rep.findOne(Id);
    }

    public Brightness addNew(PostObject<Float> e) {
        Brightness h = new Brightness(e.getValue(), e.getTimeStamp());
        return rep.save(h);
    }

    public Iterable<Brightness> addNew(List<PostObject<Float>> l) {
        ArrayList<Brightness> returnList = new ArrayList<>();
        l.stream().map((e) -> new Brightness(e.getValue(), e.getTimeStamp()))
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
        mongoTemplate.remove(new Query(), "brightnessbyhour");
        mongoTemplate.remove(new Query(), "brightnessbymonth");
        mongoTemplate.remove(new Query(), "brightnessbyday");
        mongoTemplate.remove(new Query(), "brightnessbyminute");
    }
    
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
    public List<BrightnessByInterval> findByIntervalBetween(Date start, Date end, int interval){
        Query query = Query.query(Criteria.where("date").gte(start).lte(end));
      

        
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.find(query, BrightnessByInterval.class, "brightnessbyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.find(query, BrightnessByInterval.class, "brightnessbyhour") ;
            case Calendar.MONTH: return mongoTemplate.find(query, BrightnessByInterval.class, "brightnessbymonth");
            case Calendar.MINUTE:  return mongoTemplate.find(query, BrightnessByInterval.class, "brightnessbyminute");
            default: return null;
        }
        
        
        
    }
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
     public List<BrightnessByInterval> findAllByInterval( int interval){
       
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.findAll( BrightnessByInterval.class, "brightnessbyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.findAll( BrightnessByInterval.class, "brightnessbyhour");
            case Calendar.MONTH: return mongoTemplate.findAll(BrightnessByInterval.class, "brightnessbymonth");
            case Calendar.MINUTE: return mongoTemplate.findAll( BrightnessByInterval.class, "brightnessbyminute"); 
            default: return null;
        }
    }



}
