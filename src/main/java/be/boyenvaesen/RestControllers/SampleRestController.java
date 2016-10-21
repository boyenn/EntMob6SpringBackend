/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.RestControllers;

import be.boyenvaesen.Services.EmployeeService;
import be.boyenvaesen.Models.Employee;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Boyen
 */
@RestController
public class SampleRestController {
    @Autowired
    EmployeeService service;
    
    
    @RequestMapping("/employees")
    @ResponseBody
    public ResponseEntity<List<Employee>> getAll(){
        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
        
    }
    
    @RequestMapping("/employees/{id}")
    @ResponseBody
    public ResponseEntity<Employee> getOne(@PathVariable("id") Long id){
        return new ResponseEntity<>(service.findOne(id),HttpStatus.OK);
    }
    @RequestMapping(path = "/employees",method = RequestMethod.POST)
    public ResponseEntity<Employee>  postNew(@RequestBody Employee emp){
        return new ResponseEntity<>(service.addNew(emp),HttpStatus.CREATED);
    }
}
