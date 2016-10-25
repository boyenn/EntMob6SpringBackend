package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.Humidity;
import be.boyenvaesen.Models.HumidityByInterval;
import be.boyenvaesen.Models.HumidityByMinute;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityByMinuteRepository extends CrudRepository<HumidityByMinute,Long> { 
    public List<HumidityByMinute> findByAtTimeBetween(Date start, Date end);
    @Override
    public List<HumidityByMinute> findAll();
}