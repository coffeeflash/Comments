package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.CommentCategoryCount;
import ch.tobisyurt.comments.repository.CommentsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentsService{

    @Autowired
    CommentsRepo commentsRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    public void addComment(Comment comment) {
        comment.setDate(new Date());
        LOG.info("Non-sanitized: " + comment.getComment());
        String sanitizedComment = Encode.forHtml(comment.getComment());
        LOG.info("Sanitized html: " + sanitizedComment);
        sanitizedComment = Encode.forJavaScript(comment.getComment());
        LOG.info("Sanitized javascript: " + sanitizedComment);
        comment.setComment(sanitizedComment);
        comment = commentsRepo.insert(comment);

        LOG.info("Persisted comment from user: {} on source: {} with comment_id: {}",
                comment.getUser(), comment.getSource(), comment.getId());
    }

    @Override
    public void deleteComment(String id) {
        commentsRepo.deleteById(id);
    }

    @Override
    public void replyToComment(String id, String admin, String replyText) {
        Optional<Comment> toReplyOpt = commentsRepo.findById(id);
        if(toReplyOpt.isPresent()){
            Comment c = toReplyOpt.get();
            c.setReply(replyText);
            c.setRead(true);
            c.setAdmin(admin);
            c.setReplyDate(new Date());
            commentsRepo.save(c);
        }
    }

    @Override
    public List<Comment> getComments(String source) {
        return commentsRepo.findAllBySourceOrderByDateAsc(source);
    }

    @Override
    public List<Comment> getCommentsRead(boolean read) {
        return commentsRepo.findAllByReadOrderByReadAscDateAsc(read);
    }

    @Override
    public List<CommentCategoryCount> getCommentCategoryCounts() {

        final String SOURCE = "source";
        final String SOURCE_TITLE = "sourceTitle";
        final String COUNT = "count";

        GroupOperation groupOperation = Aggregation.group(SOURCE, SOURCE_TITLE).count().as(COUNT);
        // projection operation
        ProjectionOperation projectionOperation =
                Aggregation.project(COUNT, SOURCE, SOURCE_TITLE);
        // sorting in ascending
        SortOperation sortOperation =
                Aggregation.sort(Sort.by(Sort.Direction.DESC, COUNT));
        // aggregating all 3 operations using newAggregation() function
        Aggregation aggregation =
                Aggregation.newAggregation(groupOperation, projectionOperation, sortOperation);
        // putting in a list
        AggregationResults<CommentCategoryCount> results = mongoTemplate.aggregate(aggregation,
                mongoTemplate.getCollectionName(Comment.class), CommentCategoryCount.class);

        return results.getMappedResults();
    }

    @Override
    public void setRead(String id) {
        Optional<Comment> cOpt = commentsRepo.findById(id);
        if(cOpt.isPresent()){
            Comment c = cOpt.get();
            c.setRead(!c.isRead());
            commentsRepo.save(c);
        }
    }

    @Override
    public void setAllRead() {
        List <Comment> comments = commentsRepo.findAll();
        for (Comment c : comments) {
            c.setRead(true);
        }
        commentsRepo.saveAll(comments);
    }

}
