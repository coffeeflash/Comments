package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;

public interface QuizService {
    Quiz createQuiz(int securityLevel, int validityInSeconds);
    boolean verifyQuizSolution(String quizId, String nonce);
    String toHexString(byte[] hash);
}
