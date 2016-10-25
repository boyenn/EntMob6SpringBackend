/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author Boyen
 */
@Entity
public class HumidityByMinute extends HumidityByInterval implements Serializable {

    public HumidityByMinute() {
    }

    public HumidityByMinute(Date d) {
        super(0, d);
    }

    public HumidityByMinute(float averagePercentage, Date atTime) {
        super(averagePercentage, atTime);
    }

}
