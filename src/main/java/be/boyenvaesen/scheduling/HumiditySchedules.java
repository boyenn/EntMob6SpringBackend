/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.scheduling;

import be.boyenvaesen.Models.Humidity;
import be.boyenvaesen.Models.HumidityByInterval;

import be.boyenvaesen.Services.HumidityService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.expression.Expression;

/**
 *
 * @author Boyen
 */
@Component
public class HumiditySchedules {

    private static final Logger log = LoggerFactory.getLogger(HumiditySchedules.class);
    @Autowired
    public HumidityService service;
    @Autowired
    MongoTemplate mongoTemplate;

    
    public void updateIntervalDatabases() {
        log.info("Starting database cleanup");

//      
        Calendar c = Calendar.getInstance();
//        //Clean records dating till :
        c.add(Calendar.HOUR, -3);

        Date now = new Date();
        // Retrieve humidity records from database

        AggregationResults<DBObject> resultsDB = aggregationByInterval(c.getTime(), now);
        
        List<DBObject> tagCountDB = resultsDB.getMappedResults();
        tagCountDB.forEach((dbobject) -> {
            //build object
            HumidityByInterval hbi = new HumidityByInterval();
            hbi.setDate((BasicDBObject) dbobject.get("id"));
            hbi.setAvPer((double) dbobject.get("avPer"));

            //build query
            Query query = new Query(Criteria.where("date").is(hbi.getDate()));

            //build update
            DBObject dbDoc = new BasicDBObject();
            mongoTemplate.getConverter().write(hbi, dbDoc); //Convert to DB ojectect , needed to create the update
            Update update = Update.fromDBObject(dbDoc, "_id"); //Exclude ID so it doesn't turn into null
            mongoTemplate.upsert(query, update, HumidityByInterval.class, "humiditybyhour"); // upsert = up(date) and (in)sert , similar to AddOrUpdate in C#

        });
        tagCountDB.clear();

        log.info("Ended database cleanup");

    }

    private AggregationResults<DBObject> aggregationByInterval(Date from, Date to) {

        Aggregation agg = newAggregation(
                match(Criteria.where("measured").gte(from).lte(to)),
                project("id").and("percentage").as("percentage")
                        .andExpression("year(measured)").as("jaar")
                        .andExpression("month(measured)").as("maand") // .andExpression("dayOfMonth(measured)").as("dag")
                // .andExpression("hour(measured)").as("uur"),
                // .andExpression("minute(measured)").as("minuut"),
                ,
                 group(fields("jaar", "maand", "dag", "uur")).avg("percentage").as("avPer"),
                project("avPer").and("id").previousOperation()
        );
        AggregationResults<DBObject> resultsDB = mongoTemplate.aggregate(agg, Humidity.class, DBObject.class);
        return resultsDB;
    }
    
    private void saveDBObjects(int interval){
        
    }

}
