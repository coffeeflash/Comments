package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Comment;

import java.util.List;

public interface CommentsService {
    void addComment(Comment comment, String referer);
    List<Comment> getComments(String source, String referer);
}
