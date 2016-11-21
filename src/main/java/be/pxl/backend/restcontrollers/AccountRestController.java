package be.pxl.backend.restcontrollers;

import be.pxl.backend.models.Account;
import be.pxl.backend.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

//TODO
@RestController
public class AccountRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRestController.class);

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


    @RequestMapping(value ="/changepassword/{username}",method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Account> changePassword(@PathVariable(name="username") String username , @RequestBody(required = true) String newpassword ){

        UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Get the account of that user
        Account account = accountService.findByUsername(auth.getUsername());
        LOGGER.info(account.getRoles().toString());
        LOGGER.info(auth.getAuthorities().toString());
        Account ac = accountService.updatePassword(username,newpassword);
        if(ac!=null){

            return new ResponseEntity<>(ac, HttpStatus.OK);
        }else{

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
