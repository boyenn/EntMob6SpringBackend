/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.models;

import java.util.Date;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Boyen
 */
@Document(collection = "humidities")
public class Humidity  {
    
    private @Id
    String id;
    private float percentage;

    private Date measured;

    public Humidity() {
    }

    public Humidity(float percentage, Date measured) {

        this.percentage = percentage;
        this.measured = measured;
    }

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

}
