package ch.tobisyurt.comments.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class Comment {

    @Id
    private String id;
    // auto-generated
    private Date date;
    // concat referer and blog-post title
    private String source;
    private String user;
    private String comment;

    private String quizId;
    private List<String> quizSolutions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<String> getQuizSolutions() {
        return quizSolutions;
    }

    public void setQuizSolutions(List<String> quizSolution) {
        this.quizSolutions = quizSolution;
    }
}
