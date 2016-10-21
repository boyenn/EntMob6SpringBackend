/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen;

import be.boyenvaesen.Models.Employee;
import be.boyenvaesen.Services.EmployeeService;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private EmployeeService service;

    private Employee singleEmployee;

    @Before
    public void setUp() {

        service.deleteAll();
        singleEmployee = new Employee("Boyen", "Vaesen", "Testfounder");
        service.addNew(singleEmployee);
        service.addNew(Arrays.asList(
                new Employee("Maarten", "Bloemen", "Cofounder ;)"),
                new Employee("Thijs", "Lanssens", "Cofounder ;)"),
                new Employee("Jasper", "Beuls", "ToilettenPoetser")));

    }

    @Test
    public void testSingle() {

        ResponseEntity<Employee> responseEntity
                = restTemplate.getForEntity(String.format("/employees/%s", singleEmployee.getId()), Employee.class);
        Employee employee = responseEntity.getBody();
        Assert.assertEquals("Boyen", employee.getFirstName());
    }

    @Test
    public void testList() {

        ResponseEntity<Employee[]> responseEntity
                = restTemplate.getForEntity("/employees", Employee[].class);
         assertEquals(HttpStatus.OK,responseEntity.getStatusCode() );
        Employee[] employees = responseEntity.getBody();
        assertEquals("Boyen", employees[0].getFirstName());
        assertEquals("Maarten", employees[1].getFirstName());
        assertEquals("Thijs", employees[2].getFirstName());
        assertEquals("Jasper", employees[3].getFirstName());
        assertEquals(4, employees.length);
    }
    
    @Test
    public void testScenarioPostOneGetList() {

        //Check if list is the list from setup
        ResponseEntity<Employee[]> responseEntity
                = restTemplate.getForEntity("/employees", Employee[].class);
        Employee[] employees = responseEntity.getBody();
        assertEquals("Boyen", employees[0].getFirstName());
        assertEquals(4, employees.length);

        //Post a new employee
        ResponseEntity<Employee> postedEntity = restTemplate.postForEntity("/employees", new Employee("newFirst", "newLast", "newDescr"), Employee.class);
        Employee postedEmployee = postedEntity.getBody();
        assertEquals(HttpStatus.CREATED,postedEntity.getStatusCode() );
        assertEquals("newFirst",postedEmployee.getFirstName());
        
        //Check if list is now changed with the new value
        responseEntity
                = restTemplate.getForEntity("/employees", Employee[].class);
        employees = responseEntity.getBody();
        assertEquals("Boyen", employees[0].getFirstName());
        assertEquals(postedEmployee.getFirstName(), employees[4].getFirstName());

        assertEquals(5, employees.length);

    }

}
