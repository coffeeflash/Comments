package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;

import java.util.List;

public interface QuizService {
    Quiz createQuiz(int securityLevel,int quizCount, int validityInSeconds);
    boolean verifyQuizSolution(String quizId, List<String> nonceStrings);
    String toHexString(byte[] hash);
}
