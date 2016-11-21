/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.repositories;

import be.pxl.backend.models.AirPressure;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thijs
 */
@Repository
public interface AirPressureByIntervalRepository extends MongoRepository<AirPressure,String> {
     //Find all AirPressureByInterval between 2 dates
     List<AirPressure> findByMeasuredBetween(Date start, Date end);

}
