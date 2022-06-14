package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.repository.CommentsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentsService{

    @Autowired
    CommentsRepo commentsRepo;

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
}
