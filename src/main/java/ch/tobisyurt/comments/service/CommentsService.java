package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.CommentCategoryCount;

import java.util.List;

public interface CommentsService {
    void addComment(Comment comment, String referer);
    void deleteComment(String id);
    List<Comment> getComments(String source, String referer);
    List<CommentCategoryCount> getCommentCategoryCounts();
}
