/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.scheduling;

import be.pxl.backend.models.Humidity;
import be.pxl.backend.models.HumidityByInterval;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 *
 * @author Boyen
 */
@Component
public class Schedules {


    @Autowired
    private MongoTemplate mongoTemplate;
    private static final Logger log = LoggerFactory.getLogger(Schedules.class);

    //EXECUTE BY INTERVAL IN MS
    @Scheduled(fixedRate = 10000)
    public void updateIntervalDatabases() {

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));


        c.setTime(new Date());
        c.add(Calendar.HOUR_OF_DAY,1);
        Date now = c.getTime();

        //Clean records dating till :
        c.add(Calendar.DAY_OF_MONTH, -3);
        handlePerClass(c, now,Humidity.class,HumidityByInterval.class);


    }

    private void handlePerClass(Calendar c, Date now,Class modelClass,Class intervalClass) {
        // Retrieve humidity records from database
        AggregationResults<DBObject> resultsDB = aggregationByInterval(c.getTime(), now, Calendar.MONTH,modelClass);
        //SAVE RESULTS TO DIFFERENT COLLECTION
        saveDBObjects(Calendar.MONTH, resultsDB,HumidityByInterval.class);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.DAY_OF_MONTH,modelClass);
        saveDBObjects(Calendar.DAY_OF_MONTH, resultsDB,HumidityByInterval.class);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.HOUR,modelClass);
        saveDBObjects(Calendar.HOUR, resultsDB,HumidityByInterval.class);
        resultsDB = aggregationByInterval(c.getTime(), now, Calendar.MINUTE,modelClass);
        saveDBObjects(Calendar.MINUTE, resultsDB,HumidityByInterval.class);
    }

    private AggregationResults<DBObject> aggregationByInterval(Date from, Date to, int interval,Class modelClass) {
        Aggregation agg = null;
        //CREATE THE AGGREGATION QUERY
        switch (interval) {
            case Calendar.MONTH:
                agg = newAggregation(match(Criteria.where("measured").gte(from).lte(to)),
                        monthProjection(),
                        group(fields("jaar", "maand")).avg("percentage").as("avPer"),
                        projectObject()
                );
                break;
            case Calendar.DAY_OF_MONTH:
                agg = newAggregation(match(Criteria.where("measured").gte(from).lte(to)),
                        dayProjection(),
                        group(fields("jaar", "maand", "dag")).avg("percentage").as("avPer"),
                        projectObject()
                );
                break;
            case Calendar.HOUR:
                agg = newAggregation(match(Criteria.where("measured").gte(from).lte(to)),
                        hourProjection(),
                        group(fields("jaar", "maand", "dag", "uur")).avg("percentage").as("avPer"),
                        projectObject()
                );
                break;
            case Calendar.MINUTE:
                agg = newAggregation(match(Criteria.where("measured").gte(from).lte(to)),
                        minuteProjection(),
                        group(fields("jaar", "maand", "dag", "uur", "minuut")).avg("percentage").as("avPer"),
                        projectObject()
                );
                break;

        }
        //EXECUTE THE AGGREGATION QUERY
        AggregationResults<DBObject> resultsDB = mongoTemplate.aggregate(agg, modelClass, DBObject.class);

        return resultsDB;
    }

    private static ProjectionOperation projectObject() {
        return project("avPer").and("id").previousOperation();
    }
    //PROJECTIONS USED TO CLEAN CODE
    private static ProjectionOperation minuteProjection() {
        return hourProjection()
                .andExpression("minute(measured)").as("minuut");
    }

    private static ProjectionOperation hourProjection() {
        return dayProjection()
                .andExpression("hour(measured)").as("uur");
    }

    private static ProjectionOperation dayProjection() {
        return monthProjection()
                .andExpression("dayOfMonth(measured)").as("dag");
    }

    private static ProjectionOperation monthProjection() {
        return project("id").and("percentage").as("percentage")
                .andExpression("year(measured)").as("jaar")
                .andExpression("month(measured)").as("maand");
    }

    //SAVE THE OBJECTS
    private void saveDBObjects(int interval, AggregationResults<DBObject> resultsDB,Class intervalClass) {

        List<DBObject> tagCountDB = resultsDB.getMappedResults();
        tagCountDB.forEach((dbobject) -> {
            //build object
            DBObject dbDoc = null;
            Query query = null;
            if(intervalClass == HumidityByInterval.class){
                HumidityByInterval hbi = new HumidityByInterval();
                hbi.setDateWithDBObject((BasicDBObject) dbobject.get("id"));
                hbi.setAvPer((double) dbobject.get("avPer"));
                log.info(hbi.getDate().toString());
                //build query
                query = new Query(Criteria.where("date").is(hbi.getDate()));

                //build update
                dbDoc = new BasicDBObject();

                mongoTemplate.getConverter().write(hbi, dbDoc); //Convert to DB ojectect , needed to create the update
            }
            if(dbDoc == null ||query==null){
                return;
            }



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
    }

}
