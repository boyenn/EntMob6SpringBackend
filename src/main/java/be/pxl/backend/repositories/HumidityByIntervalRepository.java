package be.pxl.backend.repositories;

import be.pxl.backend.models.Humidity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;



@Repository
public interface HumidityByIntervalRepository extends MongoRepository<Humidity,String> {
     //Find all humiditybyinterval between 2 dates
     List<Humidity> findByMeasuredBetween(Date start, Date end);

}