/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Services;

import be.boyenvaesen.Repositories.EmployeeRepository;
import be.boyenvaesen.Models.Employee;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 *
 * @author Boyen
 */
@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository rep;
    
    public List<Employee> getAll(){
        return (List<Employee>)rep.findAll();
    }
    
    public Employee findOne(long Id){
        
        return rep.findOne(Id);
    }
    public Employee addNew(Employee e){
       
        return rep.save(e);
    }
     public Iterable<Employee> addNew(List<Employee> e){
       
        return rep.save(e);
    }
     public void deleteAll(){
         rep.deleteAll();
     }
}
