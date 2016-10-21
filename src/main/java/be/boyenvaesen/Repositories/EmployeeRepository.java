package be.boyenvaesen.Repositories;

import be.boyenvaesen.Models.Employee;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    Employee findByFirstName(String firstName);
    Employee findFirstByFirstNameAndLastName(String firstName,String lastName);
    List<Employee> findByLastName(String lastName);
}