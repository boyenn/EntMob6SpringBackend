package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.Humidity;
import java.util.*;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityRepository extends CrudRepository<Humidity,Long> {
     public List<Humidity> findByMeasuredBetween(Date start, Date end);

}