package be.pxl.backend.restcontrollers;

import be.pxl.backend.models.Account;
import be.pxl.backend.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO
@RestController
public class AccountRestController {
    @Autowired

    private AccountService accountService;
    @RequestMapping(value ="/login",method = RequestMethod.GET)
    @ResponseBody

    public ResponseEntity<Account> login(@RequestParam(name="username") String username , @RequestParam(name="password") String password  ){
       Account ac = accountService.findByUsernameAndPassword(username,password);
        if(ac!=null){
            return new ResponseEntity<>(ac, HttpStatus.OK);
        }else{

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
