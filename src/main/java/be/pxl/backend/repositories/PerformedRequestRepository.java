package be.pxl.backend.repositories;

import be.pxl.backend.models.Account;
import be.pxl.backend.models.PerformedRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface PerformedRequestRepository extends MongoRepository<PerformedRequest,String> {
    //Find all performedrequest by account
    @Transactional(readOnly = true)
    List<PerformedRequest> findByAccount(Account account);

}