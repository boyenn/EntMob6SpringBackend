/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.Humidity;
import be.pxl.backend.models.HumidityByInterval;
import be.pxl.backend.services.HumidityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class HumidityRestController {
    //AUTOWIRED PROPERTIES
    @Autowired
    private HumidityService service;

    //ALL
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/humidity",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Humidity>> getAll(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/humidity/day",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getByDay(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/humidity/month",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getByMonth(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/humidity/hour",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getByHour(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/humidity/minute",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<HumidityByInterval>> getByMinute(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/humidity/latest",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Humidity> getLatest() {


            return new ResponseEntity<>(service.findLatest(), HttpStatus.OK);


    }

    //POST ONE
    @RequestMapping(path = "/humidity", method = RequestMethod.POST)
    public ResponseEntity<Humidity> postNew(@RequestBody PostObject<Float> hum) {
        return new ResponseEntity<>(service.addNew(hum), HttpStatus.CREATED);
    }
    //POST LIST
    @RequestMapping(path = "/humiditylist", method = RequestMethod.POST)
    public ResponseEntity<Iterable<Humidity>> postNew(@RequestBody List<PostObject<Float>> hum) {
        return new ResponseEntity<>(service.addNew(hum), HttpStatus.CREATED);
    }


}
