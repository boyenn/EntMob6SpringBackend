package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.HumidityByInterval;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityByIntervalRepository<T extends HumidityByInterval> extends CrudRepository<T,Long> {

}