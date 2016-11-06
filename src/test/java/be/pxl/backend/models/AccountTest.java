package be.pxl.backend.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;

/**
 * Created by Boyen on 2/11/2016.
 */
public class AccountTest {
    Account ac;
    @Before
    public void setUp() throws Exception {
         ac = new Account("Boyen","Pass",true, Arrays.asList("USER","ADMIN"));

    }

    @Test
    public void getRoles() throws Exception {
        assertThat(ac.getRoles()).containsOnly("USER","ADMIN");
    }

    @Test
    public void setRoles() throws Exception {
        assertThat(ac.getRoles()).containsOnly("USER","ADMIN");
        ac.setRoles(Arrays.asList("USER"));
        assertThat(ac.getRoles()).containsOnly("USER");
        ac.setRoles(Arrays.asList("USER","ADMIN"));
        assertThat(ac.getRoles()).containsOnly("USER","ADMIN");
    }

    @Test
    public void isEnabled() throws Exception {
        assertThat(ac.isEnabled()).isTrue();
    }

    @Test
    public void setEnabled() throws Exception {
        assertThat(ac.isEnabled()).isTrue();
        ac.setEnabled(false);
        assertThat(ac.isEnabled()).isFalse();
        ac.setEnabled(true);
        assertThat(ac.isEnabled()).isTrue();

    }

    @Test
    public void getUsername() throws Exception {
        assertThat(ac.getUsername()).isEqualToIgnoringCase("Boyen");
    }

    @Test
    public void setUsername() throws Exception {
        assertThat(ac.getUsername()).isEqualToIgnoringCase("Boyen");
        ac.setUsername("Boyenn");
        assertThat(ac.getUsername()).isEqualToIgnoringCase("Boyenn");
        ac.setUsername("Boyen");
    }
    @Test
    public void getPassword() throws Exception {
        assertThat(ac.getPassword()).isEqualTo("Pass");


    }

    @Test
    public void setPassword() throws Exception {
        assertThat(ac.getPassword()).isEqualTo("Pass");
        ac.setPassword("Pass2");
        assertThat(ac.getPassword()).isEqualTo("Pass2");
        ac.setPassword("Pass");
        assertThat(ac.getPassword()).isEqualTo("Pass");
    }

}