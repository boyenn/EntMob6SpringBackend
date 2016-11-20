/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Temperature;
import be.pxl.backend.models.TemperatureByInterval;
import be.pxl.backend.services.TemperatureService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thijs
 */
@RestController
public class TemperatureRestController {
    //AUTOWIRED PROPERTIES
    @Autowired
    private TemperatureService service;

    //ALL
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/temperature",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Temperature>> getAll(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
                                                 @RequestParam(name = "end",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date end) {

        if(start!=null && end!=null){
                return new ResponseEntity<>(service.getBetweenDates(start,end), HttpStatus.OK);
        }
        else if (start==null&&end==null){
            return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body(null);
        }


    }
    //BY DAY
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/temperature/day",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TemperatureByInterval>> getByDay(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
                                                               @RequestParam(name = "end",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date end) {

        if(start!=null && end!=null){
            return new ResponseEntity<>(service.findByIntervalBetween(start,end,Calendar.DAY_OF_MONTH), HttpStatus.OK);
        }
        else if (start==null&&end==null){
            return new ResponseEntity<>(service.findAllByInterval(Calendar.DAY_OF_MONTH), HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body(null);
        }


    }
    // BY MONTH
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/temperature/month",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TemperatureByInterval>> getByMonth(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
                                                               @RequestParam(name = "end",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date end) {

        if(start!=null && end!=null){
            return new ResponseEntity<>(service.findByIntervalBetween(start,end,Calendar.MONTH), HttpStatus.OK);
        }
        else if (start==null&&end==null){
            return new ResponseEntity<>(service.findAllByInterval(Calendar.MONTH), HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body(null);
        }


    }

    //HOUR
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/temperature/hour",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TemperatureByInterval>> getByHour(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
                                                               @RequestParam(name = "end",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date end) {

        if(start!=null && end!=null){
            return new ResponseEntity<>(service.findByIntervalBetween(start,end,Calendar.HOUR_OF_DAY), HttpStatus.OK);
        }
        else if (start==null&&end==null){
            return new ResponseEntity<>(service.findAllByInterval(Calendar.HOUR_OF_DAY), HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body(null);
        }


    }
    //BY MINUTE
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/temperature/minute",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TemperatureByInterval>> getByMinute(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
                                                              @RequestParam(name = "end",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date end) {

        if(start!=null && end!=null){
            return new ResponseEntity<>(service.findByIntervalBetween(start,end,Calendar.MINUTE), HttpStatus.OK);
        }
        else if (start==null&&end==null){
            return new ResponseEntity<>(service.findAllByInterval(Calendar.MINUTE), HttpStatus.OK);
        }else{
            return ResponseEntity.badRequest().body(null);
        }


    }
    //LATEST
    @RequestMapping(value ="/temperature/latest",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Temperature> getLatest() {


            return new ResponseEntity<>(service.findLatest(), HttpStatus.OK);


    }

    //POST ONE
    @RequestMapping(path = "/temperature", method = RequestMethod.POST)
    public ResponseEntity<Temperature> postNew(@RequestBody PostObject<Float> temp) {
        return new ResponseEntity<>(service.addNew(temp), HttpStatus.CREATED);
    }
    //POST LIST
    @RequestMapping(path = "/temperaturelist", method = RequestMethod.POST)
    public ResponseEntity<Iterable<Temperature>> postNew(@RequestBody List<PostObject<Float>> temp) {
        return new ResponseEntity<>(service.addNew(temp), HttpStatus.CREATED);
    }


}
