/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen.Services;

import be.boyenvaesen.Models.Account;
import be.boyenvaesen.Repositories.AccountRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Boyen
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            List<String> rolesList = account.getRoles();
            String[] roles = rolesList.toArray(new String[rolesList.size()]);
            return new User(account.getUsername(), account.getPassword(), account.isEnabled(), true, true, true,
                    AuthorityUtils.createAuthorityList((roles)));
        } else {
            throw new UsernameNotFoundException("could not find the user '"
                    + username + "'");
        }
    }
}
