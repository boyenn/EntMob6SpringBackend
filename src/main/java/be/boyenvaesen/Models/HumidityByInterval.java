/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Models;

import com.mongodb.BasicDBObject;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class HumidityByInterval {

    @Id
    private String id;
    private Date date;
    private double avPer;
    private static final Logger LOGGER = LoggerFactory.getLogger(HumidityByInterval.class);

    public HumidityByInterval() {
        LOGGER.info("HumidityByInterval()");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(BasicDBObject dateObject) {
        LOGGER.info("setid");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.YEAR, dateObject.getInt("jaar"));
        c.set(Calendar.MONTH, dateObject.getInt("maand"));
        c.set(Calendar.DAY_OF_MONTH, tryOrNull(dateObject, "dag"));
        c.set(Calendar.HOUR, tryOrNull(dateObject, "uur"));
        c.set(Calendar.MINUTE, tryOrNull(dateObject, "minuut"));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.date = c.getTime();
    }

    public void setDate(Date d) {
        this.date = d;
    }

    public double getAvPer() {
        return avPer;
    }

    public void setAvPer(double avPer) {
        this.avPer = avPer;
    }

    @Override
    public String toString() {
        return "HumidityByInterval{" + "id=" + id + ", date=" + date + ", avPer=" + avPer + '}';
    }

    private int tryOrNull(BasicDBObject db, String field) {
        try {
            return db.getInt(field);
        } catch (NullPointerException e) {

        }
        return 0;
    }

  
}
