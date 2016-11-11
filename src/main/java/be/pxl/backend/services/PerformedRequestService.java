package be.pxl.backend.services;

import be.pxl.backend.models.Account;
import be.pxl.backend.models.PerformedRequest;
import be.pxl.backend.repositories.PerformedRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by Boyen on 2/11/2016.
 */
@Service
public class PerformedRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformedRequestService.class);


    @Autowired
    private PerformedRequestRepository performedRequestRepository;

    @Value("${backendratelimiter.refreshinterval}")
    private int refreshInterval;
    @Value("${backendratelimiter.maxrequests}")
    private int maxRequests;


    public Boolean ExecuteRequest(Account account,String signature) {
        Calendar c = Calendar.getInstance();

        c.add(Calendar.SECOND,0-refreshInterval);

        Boolean b = performedRequestRepository.findByAccount(account).stream().filter(performedRequest -> performedRequest.getCreatedDate().after(c.getTime())).count() <maxRequests;

        if(b){

         performedRequestRepository.save(new PerformedRequest(account,signature));

        }
        return b;


    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAll() {
        performedRequestRepository.deleteAll();
    }
}
