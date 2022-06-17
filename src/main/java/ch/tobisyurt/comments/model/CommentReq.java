package ch.tobisyurt.comments.model;

import org.springframework.data.annotation.Transient;

import java.util.List;

public class CommentReq extends Comment{

    // gets not sent to db with "Transient"
    @Transient
    private String quizId;
    @Transient
    private List<String> quizSolutions;

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
