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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 *
 * @author Boyen
 */
@Component
public class HumiditySchedules {

    private static final Logger LOGGER = LoggerFactory.getLogger(HumiditySchedules.class);
    @Autowired
    public HumidityService service;
    @Autowired
    MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 2000)
    public void updateIntervalDatabases() {
        LOGGER.info("Starting database cleanup");
        LOGGER.info("Working on database : " + mongoTemplate.getDb().getName());
//      
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        
       
        c.setTime(new Date());
        Date now = c.getTime();
        
//        //Clean records dating till :
        c.add(Calendar.DAY_OF_MONTH, -3);
 c.add(Calendar.YEAR, -3);
        LOGGER.info(c.getTime().toString());
        
        // Retrieve humidity records from database

        AggregationResults<DBObject> resultsDB = aggregationByInterval(c.getTime(), now, Calendar.MONTH);
        saveDBObjects(Calendar.MONTH, resultsDB);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.DAY_OF_MONTH);
        saveDBObjects(Calendar.DAY_OF_MONTH, resultsDB);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.HOUR);
        saveDBObjects(Calendar.HOUR, resultsDB);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.MINUTE);
        saveDBObjects(Calendar.MINUTE, resultsDB);

        LOGGER.info("Ended database cleanup");

    }

    private AggregationResults<DBObject> aggregationByInterval(Date from, Date to, int interval) {
        Aggregation agg = null;
        LOGGER.info(from.toString() + " TO " + to.toString());
        
        switch (interval) {
            case Calendar.MONTH:
                agg = newAggregation(
                        match(Criteria.where("measured").gte(from).lte(to)),
                        project("id").and("percentage").as("percentage")
                                .andExpression("year(measured)").as("jaar")
                                .andExpression("month(measured)").as("maand"),
                        group(fields("jaar", "maand")).avg("percentage").as("avPer"),
                        project("avPer").and("id").previousOperation()
                );
                break;
            case Calendar.DAY_OF_MONTH:
                agg = newAggregation(
                       match(Criteria.where("measured").gte(from).lte(to)),
                        project("id").and("percentage").as("percentage")
                                .andExpression("year(measured)").as("jaar")
                                .andExpression("month(measured)").as("maand")
                                .andExpression("dayOfMonth(measured)").as("dag"),
                        group(fields("jaar", "maand", "dag")).avg("percentage").as("avPer"),
                        project("avPer").and("id").previousOperation()
                );
                break;
            case Calendar.HOUR:
                agg = newAggregation(
                        match(Criteria.where("measured").gte(from).lte(to)),
                        project("id").and("percentage").as("percentage")
                                .andExpression("year(measured)").as("jaar")
                                .andExpression("month(measured)").as("maand")
                                .andExpression("dayOfMonth(measured)").as("dag")
                                .andExpression("hour(measured)").as("uur"),
                        group(fields("jaar", "maand", "dag", "uur")).avg("percentage").as("avPer"),
                        project("avPer").and("id").previousOperation()
                );
                break;
            case Calendar.MINUTE:
                agg = newAggregation(
                        match(Criteria.where("measured").gte(from).lte(to)),
                        project("id").and("percentage").as("percentage")
                                .andExpression("year(measured)").as("jaar")
                                .andExpression("month(measured)").as("maand")
                                .andExpression("dayOfMonth(measured)").as("dag")
                                .andExpression("hour(measured)").as("uur")
                                .andExpression("minute(measured)").as("minuut"),
                        group(fields("jaar", "maand", "dag", "uur", "minuut")).avg("percentage").as("avPer"),
                        project("avPer").and("id").previousOperation()
                );
                break;

        }
        LOGGER.info("" + interval);
        LOGGER.info(String.valueOf(agg == null));
        
        AggregationResults<DBObject> resultsDB = mongoTemplate.aggregate(agg, Humidity.class, DBObject.class);

        return resultsDB;
    }

    private void saveDBObjects(int interval, AggregationResults<DBObject> resultsDB) {

        List<DBObject> tagCountDB = resultsDB.getMappedResults();
        LOGGER.info("size = " + tagCountDB.size());
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
            switch (interval) {
                case Calendar.MONTH:
                    mongoTemplate.upsert(query, update, HumidityByInterval.class, "humiditybymonth"); // upsert = up(date) and (in)sert , similar to AddOrUpdate in C#
                    break;
                case Calendar.DAY_OF_MONTH:
                    mongoTemplate.upsert(query, update, HumidityByInterval.class, "humiditybyday"); // upsert = up(date) and (in)sert , similar to AddOrUpdate in C#
                    break;
                case Calendar.HOUR:
                    mongoTemplate.upsert(query, update, HumidityByInterval.class, "humiditybyhour"); // upsert = up(date) and (in)sert , similar to AddOrUpdate in C#
                    break;
                case Calendar.MINUTE:
                    mongoTemplate.upsert(query, update, HumidityByInterval.class, "humiditybyminute"); // upsert = up(date) and (in)sert , similar to AddOrUpdate in C#
                    break;
            }
        });
        //tagCountDB.clear();
    }

}
