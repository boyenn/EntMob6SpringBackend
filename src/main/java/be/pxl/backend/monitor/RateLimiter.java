package be.pxl.backend.monitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by Boyen on 9/11/2016.
 */
@Aspect
@Component
public class RateLimiter {


    @Value("${backendratelimiter.refreshinterval}")
    private int refreshInterval;

    @Value("${backendratelimiter.maxrequests}")
    private int maxRequests;
    @Around("execution(* be.pxl.backend.restcontrollers.*.*(..))")
    public void logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        joinPoint.proceed();

    }

}
