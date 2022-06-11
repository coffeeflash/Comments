package ch.tobisyurt.comments.repository;

import ch.tobisyurt.comments.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepo extends MongoRepository<Comment, String> {

}
