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

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentsService{

    @Autowired
    CommentsRepo commentsRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    public void addComment(Comment comment, String referer) {
        comment.setDate(new Date());
        comment.setSource(referer + comment.getSource());
        comment = commentsRepo.insert(comment);
        LOG.info("Persisted comment from user: {} on source: {} with comment_id: {}",
                comment.getUser(), comment.getSource(), comment.getId());
    }

    @Override
    public List<Comment> getComments(String source, String referer) {
        return commentsRepo.findAllBySourceOrderByDateAsc(referer + source);
    }

    @Override
    public List<CommentCategoryCount> getCommentCategoryCounts() {

        final String SOURCE = "source";
        final String COUNT = "count";

        GroupOperation groupOperation = Aggregation.group(SOURCE).count().as(COUNT);
        // projection operation
        ProjectionOperation projectionOperation =
                Aggregation.project(COUNT).and(SOURCE).previousOperation();
        // sorting in ascending
        SortOperation sortOperation =
                Aggregation.sort(Sort.by(Sort.Direction.ASC, COUNT));
        // aggregating all 3 operations using newAggregation() function
        Aggregation aggregation =
                //Aggregation.newAggregation(groupOperation, projectionOperation, sortOperation);
                Aggregation.newAggregation(groupOperation, projectionOperation);
        // putting in a list
        AggregationResults<CommentCategoryCount> results = mongoTemplate.aggregate(aggregation,
                mongoTemplate.getCollectionName(Comment.class), CommentCategoryCount.class);

        return results.getMappedResults();
    }

}
