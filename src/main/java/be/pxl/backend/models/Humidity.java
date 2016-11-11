/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 *
 * @author Boyen
 */
@Document(collection = "humidities")
public class Humidity  {
    //PROPERTIES
    @Id
    private String id;
    private float percentage;
    private Date measured;
    private static final Logger LOGGER = LoggerFactory.getLogger(Humidity.class);


    public Humidity() {
    }

    public Humidity(float percentage, Date measured) {

        this.percentage = percentage;
        this.measured = measured;
    }


    //GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public Date getMeasured() {
        return measured;
    }

    public void setMeasured(Date measured) {
        this.measured = measured;
    }

    public void setId(String id) {
        this.id = id;
    }
}
