/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Boyen
 */
@Entity
@Table(name = "humidities")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public class Humidity implements Serializable{
    private @Id @GeneratedValue(strategy = GenerationType.TABLE)  Long id;
    private float percentage;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date measured;

    public Humidity() {
    }

    public Humidity( float percentage, Date timeDate) {
        
        this.percentage = percentage;
        this.measured = timeDate;
    }

    public Long getId() {
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
