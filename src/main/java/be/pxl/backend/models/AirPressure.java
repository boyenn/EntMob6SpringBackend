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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * @author Thijs
 */
@Document(collection = "airpressures")
@Transactional
public class AirPressure  {
    //PROPERTIES
    @Id
    private String id;
    private float value;
    private Date measured;
    private static final Logger LOGGER = LoggerFactory.getLogger(AirPressure.class);


    public AirPressure() {
    }

    public AirPressure(float value, Date measured) {

        this.value = value;
        this.measured = measured;
    }


    //GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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
