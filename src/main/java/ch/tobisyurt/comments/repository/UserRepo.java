package ch.tobisyurt.comments.repository;

import ch.tobisyurt.comments.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository <User, String> {
    User findByUsername(String username);
}
