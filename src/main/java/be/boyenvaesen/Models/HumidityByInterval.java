/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Boyen
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class HumidityByInterval implements Serializable {

    private float averagePercentage;
    private @Id
            @Temporal(TemporalType.TIMESTAMP)
    Date atTime;

    public HumidityByInterval(float averagePercentage, Date atTime) {
        this.averagePercentage = averagePercentage;
        this.atTime = atTime;
    }

    public HumidityByInterval() {
    }

    public float getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(float averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public Date getAtTime() {
        return atTime;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }
    

}
