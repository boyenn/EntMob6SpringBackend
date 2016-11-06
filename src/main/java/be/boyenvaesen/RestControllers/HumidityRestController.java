/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.RestControllers;

import be.boyenvaesen.Services.HumidityService;
import be.boyenvaesen.Models.Humidity;
import be.boyenvaesen.Models.HumidityByInterval;
import be.boyenvaesen.helpers.postObject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Boyen
 */
@RestController
public class HumidityRestController {

    @Autowired
    HumidityService service;

    @RequestMapping(value = "/humidity",method = RequestMethod.GET)
    @PreAuthorize("USER")
    @ResponseBody
    public ResponseEntity<List<Humidity>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);

    }

    @RequestMapping(value ="/humidity/month",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getAllByMonth() {
        return new ResponseEntity<>(service.findAllByInterval(Calendar.MONTH), HttpStatus.OK);

    }
    
    @RequestMapping(value ="/humidity/hour",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getAllByHour() {
        return new ResponseEntity<>(service.findAllByInterval(Calendar.HOUR), HttpStatus.OK);

    }
    
    @RequestMapping(value ="/humidity/minute",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getAllByMinute() {
        return new ResponseEntity<>(service.findAllByInterval(Calendar.MINUTE), HttpStatus.OK);

    }


    @RequestMapping(path = "/humidity", method = RequestMethod.POST)
    public ResponseEntity<Humidity> postNew(@RequestBody postObject<Float> hum) {
        return new ResponseEntity<>(service.addNew(hum), HttpStatus.CREATED);
    }
}
