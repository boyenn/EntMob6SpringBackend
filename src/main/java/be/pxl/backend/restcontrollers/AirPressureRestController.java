/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.restcontrollers;

import be.pxl.backend.helpers.PostObject;
import be.pxl.backend.models.AirPressure;
import be.pxl.backend.models.AirPressureByInterval;
import be.pxl.backend.services.AirPressureService;
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
public class AirPressureRestController {
    //AUTOWIRED PROPERTIES
    @Autowired
    private AirPressureService service;

    //ALL
    //OPTIONAL : BETWEEN DATES
    @RequestMapping(value ="/airpressure",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AirPressure>> getAll(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/airpressure/day",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AirPressureByInterval>> getByDay(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/airpressure/month",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AirPressureByInterval>> getByMonth(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/airpressure/hour",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AirPressureByInterval>> getByHour(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/airpressure/minute",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<AirPressureByInterval>> getByMinute(@RequestParam(name= "start",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  Date start,
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
    @RequestMapping(value ="/airpressure/latest",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AirPressure> getLatest() {


            return new ResponseEntity<>(service.findLatest(), HttpStatus.OK);


    }

    //POST ONE
    @RequestMapping(path = "/airpressure", method = RequestMethod.POST)
    public ResponseEntity<AirPressure> postNew(@RequestBody PostObject<Float> hum) {
        return new ResponseEntity<>(service.addNew(hum), HttpStatus.CREATED);
    }
    //POST LIST
    @RequestMapping(path = "/airpressurelist", method = RequestMethod.POST)
    public ResponseEntity<Iterable<AirPressure>> postNew(@RequestBody List<PostObject<Float>> hum) {
        return new ResponseEntity<>(service.addNew(hum), HttpStatus.CREATED);
    }
}
