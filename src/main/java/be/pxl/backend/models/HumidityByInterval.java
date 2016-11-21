/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.models;

import be.pxl.backend.helpers.DBObjectToDate;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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

        this.date = DBObjectToDate.dbObjectToDate(dateObject);
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
