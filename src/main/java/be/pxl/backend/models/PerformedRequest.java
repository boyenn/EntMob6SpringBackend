package be.pxl.backend.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Document(collection = "performedrequests")
@Transactional
public class PerformedRequest {
    //PROPERTIES
    @Id
    private String id;
    private String map;
    @CreatedDate
    private Date createdDate;
    @DBRef
    private Account account;

    private static final Logger LOGGER = LoggerFactory.getLogger(HumidityByInterval.class);

    public PerformedRequest(Account account,String map) {
        this.account = account;
        this.map = map;
    }



    //GETTERS AND SETTERS
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    @Override
    public String toString() {
        return "PerformedRequest{" +
                "id='" + id + '\'' +
                ", map='" + map + '\'' +
                ", createdDate=" + createdDate +
                ", account=" + account +
                '}';
    }
}
