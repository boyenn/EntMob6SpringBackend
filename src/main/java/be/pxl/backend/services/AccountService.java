package be.pxl.backend.services;

import be.pxl.backend.models.Account;
import be.pxl.backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by Boyen on 2/11/2016.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Account addAccount(Account account){
        return accountRepository.save(account);

    }
    public AccountService(){

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAll() {
        accountRepository.deleteAll();
    }
}
