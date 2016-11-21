package be.pxl.backend.monitor;

import be.pxl.backend.models.Account;
import be.pxl.backend.services.AccountService;
import be.pxl.backend.services.PerformedRequestService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class RateLimiter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiter.class);

    //VALUES IN application.properties

    @Value("${backendratelimiter.refreshinterval}")
    private int refreshInterval;
    @Value("${backendratelimiter.maxrequests}")
    private int maxRequests;

    //AUTOWIRED PROPERTIES
    @Autowired
    private AccountService accountService;
    @Autowired
    private PerformedRequestService performedRequestService;

    //Performs once around every method in the restcontrollers package
    @Around("execution(* be.pxl.backend.restcontrollers.*.*(..))")
    public Object  logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        //What the proxy will return
        Object retVal ;
        //Get the userdetails (Spring)  of the user performing the request
        try{
            UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Get the account of that user
            Account account = accountService.findByUsername(auth.getUsername());
            //Checks whether or not the request is allowed to be executed and if it is, executes it and saves the request to the database
            Boolean isAllowedToMakeRequest = performedRequestService.ExecuteRequest(account,joinPoint.getSignature().toLongString());


            if(isAllowedToMakeRequest){
                //Proceed the request as usual
                retVal = joinPoint.proceed();
            }
            else {
                //If the user has done too many requests, log that he tried again and return TOO_MANY_REQUESTS status code
                LOGGER.info(auth.getUsername() + " has done too many requests");
                retVal = new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
            }

        }catch (Exception ex){
            retVal = joinPoint.proceed();
        }

        return retVal;
    }

}
