package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.HumidityByHour;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface HumidityByHourRepository extends CrudRepository<HumidityByHour,Long> {
    public List<HumidityByHour> findByAtTimeBetween(Date start, Date end);
    @Override
    public List<HumidityByHour> findAll();
}