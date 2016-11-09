package be.pxl.backend.repositories;

import be.pxl.backend.models.Humidity;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityRepository extends MongoRepository<Humidity,String> {
      List<Humidity> findByMeasuredBetween(Date start, Date end);

}