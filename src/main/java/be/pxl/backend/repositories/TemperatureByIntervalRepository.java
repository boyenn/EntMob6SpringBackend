/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.repositories;

import be.pxl.backend.models.Temperature;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Thijs
 */
@Repository
public interface TemperatureByIntervalRepository extends MongoRepository<Temperature,String> {
     //Find all temperatureinterval between 2 dates
     @Transactional(readOnly = true)
     List<Temperature> findByMeasuredBetween(Date start, Date end);

}
