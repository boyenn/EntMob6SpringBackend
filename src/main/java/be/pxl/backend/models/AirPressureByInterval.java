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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * @author Thijs
 */


@Document
@Transactional
public class AirPressureByInterval {
      //PROPERTIES
    @Id
    private String id;
    private Date date;
    private double avVal;

    private static final Logger LOGGER = LoggerFactory.getLogger(AirPressureByInterval.class);

    public AirPressureByInterval() {

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

    public double getAvVal() {
        return avVal;
    }

    public void setAvVal(double avVal) {
        this.avVal = avVal;
    }


    @Override
    public String toString() {
        return "AirPressureByInterval{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", avVal=" + avVal +
                '}';
    }
}
