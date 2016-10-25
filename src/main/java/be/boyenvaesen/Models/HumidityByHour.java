
package be.boyenvaesen.Models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author Boyen
 */
@Entity
public class HumidityByHour extends HumidityByInterval implements Serializable{

    public HumidityByHour() {
    }
    public HumidityByHour(Date d){
        super(0,d);
    }

    public HumidityByHour(float averagePercentage, Date atTime) {
        super(averagePercentage, atTime);
    }
    
    
}
