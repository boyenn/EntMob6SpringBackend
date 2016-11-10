package be.pxl.backend.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Boyen on 9/11/2016.
 */
@Document(collection = "performedrequests")
public class PerformedRequest {
    @Id
    private String id;
    private String map;
    @CreatedDate
    private Date createdDate;
    @DBRef
    private Account account;

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

    public PerformedRequest(Account account,String map) {
        this.account = account;
        this.map = map;
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
