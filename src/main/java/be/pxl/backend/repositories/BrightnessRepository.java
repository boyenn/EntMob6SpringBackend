/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.repositories;

import be.pxl.backend.models.Brightness;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thijs
 */
@Repository
public interface BrightnessRepository extends MongoRepository<Brightness,String> {
      //Find all brightness between 2 dates
      List<Brightness> findByMeasuredBetween(Date start, Date end);
      //Find Latest brightness
      Brightness findTop1ByOrderByMeasuredDesc();
}