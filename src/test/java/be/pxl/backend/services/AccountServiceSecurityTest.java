package be.pxl.backend.services;

import be.pxl.backend.models.Account;
import be.pxl.backend.repositories.AccountRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Boyen on 1/11/2016.
     */
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("test")
    @ContextConfiguration
public class AccountServiceSecurityTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Before
    public void setUp() throws Exception {
        accountRepository.deleteAll();
    }
    @After
    public void tearDown(){
        accountRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"USER","ADMIN"})
    public void addAccountWithAdmin() throws Exception {
        TestCreateUSer();

    }


    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = {"USER"})
    public void addAccountWithUser(){
        TestCreateUSer();

    }
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void addAccountWithoutUser(){
        TestCreateUSer();

    }
    private void TestCreateUSer() {
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        Account normalUser = new Account("testUser","testPassword",true,roles);
        Account createdUser = accountService.addAccount(normalUser);
        assertEquals(normalUser.getPassword(),createdUser.getPassword());
        assertEquals(normalUser.getUsername(),createdUser.getUsername());
        assertEquals(normalUser.getRoles(),createdUser.getRoles());
    }


}