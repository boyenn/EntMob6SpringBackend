/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.services;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.AirPressure;
import be.pxl.backend.models.AirPressureByInterval;
import be.pxl.backend.repositories.AirPressureRepository;
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
public class AirPressureService {
    //AUTOWIRED PROPERTIES
    @Autowired
    private AirPressureRepository rep;
    
    @Autowired 
    private MongoTemplate mongoTemplate;

    public List<AirPressure> getAll() {
        return rep.findAll();
    }

    public List<AirPressure> getBetweenDates(Date start, Date end) {
        return rep.findByMeasuredBetween(start, end);
    }
    public AirPressure findLatest(){
        return rep.findTop1ByOrderByMeasuredDesc();
    }

    public AirPressure findOne(String Id) {

        return rep.findOne(Id);
    }

    public AirPressure addNew(PostObject<Float> e) {
        AirPressure h = new AirPressure(e.getValue(), e.getTimeStamp());
        return rep.save(h);
    }

    public Iterable<AirPressure> addNew(List<PostObject<Float>> l) {
        ArrayList<AirPressure> returnList = new ArrayList<>();
        l.stream().map((e) -> new AirPressure(e.getValue(), e.getTimeStamp()))
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
        mongoTemplate.remove(new Query(), "airpressurebyhour");
        mongoTemplate.remove(new Query(), "airpressurebymonth");
        mongoTemplate.remove(new Query(), "airpressurebyday");
        mongoTemplate.remove(new Query(), "airpressurebyminute");
    }
    
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
    public List<AirPressureByInterval> findByIntervalBetween(Date start, Date end, int interval){
        Query query = Query.query(Criteria.where("date").gte(start).lte(end));
      

        
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.find(query, AirPressureByInterval.class, "airpressurebyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.find(query, AirPressureByInterval.class, "airpressurebyhour") ;
            case Calendar.MONTH: return mongoTemplate.find(query, AirPressureByInterval.class, "airpressurebymonth");
            case Calendar.MINUTE:  return mongoTemplate.find(query, AirPressureByInterval.class, "airpressurebyminute");
            default: return null;
        }
        
        
        
    }
    //USE mongoTemplate, cannot use CRUD repositories because you want to define the collection name
     public List<AirPressureByInterval> findAllByInterval( int interval){
       
        switch(interval){
            case Calendar.DAY_OF_MONTH:  return mongoTemplate.findAll( AirPressureByInterval.class, "airpressurebyday");
            case Calendar.HOUR_OF_DAY: return mongoTemplate.findAll( AirPressureByInterval.class, "airpressurebyhour");
            case Calendar.MONTH: return mongoTemplate.findAll( AirPressureByInterval.class, "airpressurebymonth");
            case Calendar.MINUTE: return mongoTemplate.findAll( AirPressureByInterval.class, "airpressurebyminute"); 
            default: return null;
        }
    }
}
