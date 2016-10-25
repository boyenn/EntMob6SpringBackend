/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.RestControllers;

import be.boyenvaesen.Services.HumidityService;
import be.boyenvaesen.Models.Humidity;
import be.boyenvaesen.Models.HumidityByMinute;
import be.boyenvaesen.helpers.postObject;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Boyen
 */
@RestController
public class HumidityRestController {
    @Autowired
    HumidityService service;
    
    
    @RequestMapping("/humidity")
    @ResponseBody
    public ResponseEntity<List<Humidity>> getAll(){
        return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
        
    }
    @RequestMapping("/humidity/minute")
    @ResponseBody
    public ResponseEntity<List<HumidityByMinute>> getByMinute(){
        
        return new ResponseEntity<>(service.getBetweenDatesByInterval(HumidityByMinute.class, new Date(1476391394000L), new Date()),HttpStatus.OK);
        
    }
    
//    @RequestMapping("/employees/{id}")
//    @ResponseBody
//    public ResponseEntity<Employee> getOne(@PathVariable("id") Long id){
//        return new ResponseEntity<>(service.findOne(id),HttpStatus.OK);
//    }
    @RequestMapping(path = "/humidity",method = RequestMethod.POST)
    public ResponseEntity<Humidity>  postNew(@RequestBody postObject<Float> hum){
        return new ResponseEntity<>(service.addNew(hum),HttpStatus.CREATED);
    }
}
