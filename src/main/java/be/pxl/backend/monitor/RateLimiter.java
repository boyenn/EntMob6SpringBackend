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

    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    @Value("${backendratelimiter.refreshinterval}")
    private   int refreshInterval;
    @Value("${backendratelimiter.maxrequests}")
    private int maxRequests;
    @Autowired
    AccountService accountService;
    @Autowired
    PerformedRequestService performedRequestService;


    @Around("execution(* be.pxl.backend.restcontrollers.*.*(..))")
    public Object  logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("logServiceAccess");
       UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //logger.info(auth.getUsername() + " Just accessed the rest controller!");
        //TODO implement requests
        Account account = accountService.findByUsername(auth.getUsername());
        Boolean isAllowedToMakeRequest = performedRequestService.ExecuteRequest(account,joinPoint.getSignature().toLongString());
        //isAllowedToMakeRequest=true;
        Object retVal ;
        if(isAllowedToMakeRequest){
            retVal = joinPoint.proceed();
        }
        else {
            logger.info("Too many request");
            retVal = new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
       // logger.info(account.getUsername() + " Just accessed the rest controller!");
        // start stopwatch




        // stop stopwatch
        return retVal;
    }

}
