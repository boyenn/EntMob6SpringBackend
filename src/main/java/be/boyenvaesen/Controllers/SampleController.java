/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Boyen
 */
@Controller
public class SampleController {
    
    /*
    Difference between pathvariable and requestparam: 
    requestparam : /employees/{id} <- in URL hierarchy
    pathvariable : /employees?id=9 <- as ? param
    
    requestparam mag leeg zijn , pathvariable niet!
    
    */
    
    
    
    @RequestMapping("/helloworld")
    @ResponseBody
    public String helloWorldWithParam(@RequestParam("name") String name ){
        return String.format("Hello %s!",name);
    }
    
    
    
    @RequestMapping("/helloworld/{name}")
    @ResponseBody
    public String helloWorldWithPathVariable(@PathVariable("name") String name){
        return String.format("Hello %s!",name);
    }
    
    
}
