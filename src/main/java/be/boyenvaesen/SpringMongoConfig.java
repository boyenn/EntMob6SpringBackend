/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.boyenvaesen;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author Boyen
 */
@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration{

   
    

    @Override
    protected String getDatabaseName() 
    {
        return "sensordb";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception 
    {
        return new MongoClient("localhost" , 27017 );
    }
    
    public @Bean@Override 
    MongoTemplate mongoTemplate() throws Exception 
    {
        return new MongoTemplate(mongo(), getDatabaseName());
    }    

}
