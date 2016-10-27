package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.Humidity;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityByIntervalRepository extends MongoRepository<Humidity,String> {
     public List<Humidity> findByMeasuredBetween(Date start, Date end);

}