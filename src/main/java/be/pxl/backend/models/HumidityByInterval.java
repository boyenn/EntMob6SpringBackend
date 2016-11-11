/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.models;

import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Document
public class HumidityByInterval {
    //PROPERTIES
    @Id
    private String id;
    private Date date;
    private double avPer;

    private static final Logger LOGGER = LoggerFactory.getLogger(HumidityByInterval.class);

    public HumidityByInterval() {

    }


    public void setDateWithDBObject(BasicDBObject dateObject) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.YEAR, dateObject.getInt("jaar"));
        c.set(Calendar.MONTH, dateObject.getInt("maand")-1);
        int dayOfMonth = tryOrNull(dateObject, "dag");
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth==0?1: dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, tryOrNull(dateObject, "uur"));
        c.set(Calendar.MINUTE, tryOrNull(dateObject, "minuut"));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        this.date = c.getTime();
    }

    //GETTERS AND SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
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

    private int tryOrNull(BasicDBObject db, String field) {
        try {
            return db.getInt(field);
        } catch (NullPointerException ignored) {

        }
        return 0;
    }

    @Override
    public String toString() {
        return "HumidityByInterval{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", avPer=" + avPer +
                '}';
    }
}
