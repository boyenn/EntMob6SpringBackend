package be.pxl.backend.services;

import be.pxl.backend.models.Account;
import be.pxl.backend.repositories.AccountRepository;
import be.pxl.backend.repositories.PerformedRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by Boyen on 2/11/2016.
 */
@Service
public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PerformedRequestRepository performedRequestRepository;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Account addAccount(Account account){
        return accountRepository.save(account);

    }
    public Account findByUsername(String username){
        return accountRepository.findByUsername(username);
    }

    public Account findByUsernameAndPassword(String username,String password){
        return accountRepository.findByUsernameAndPassword(username,password);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAll() {
        accountRepository.deleteAll();
    }
}
