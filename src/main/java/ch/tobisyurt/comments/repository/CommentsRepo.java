package ch.tobisyurt.comments.repository;

import ch.tobisyurt.comments.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepo extends MongoRepository<Comment, String> {
    List<Comment> findAllBySourceOrderByDateAsc(String source);
    List<Comment> findAllByReadOrderByReadAscDateAsc(boolean read);
    List<Comment> findAll();
}
