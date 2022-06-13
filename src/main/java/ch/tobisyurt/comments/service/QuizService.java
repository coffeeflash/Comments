package ch.tobisyurt.comments.service;

public interface QuizService {

    public String createQuiz(String quizId, int securityLevel, int validityInSeconds);
    public boolean verifyQuizSolution(String quizId, String nonce);
    public String toHexString(byte[] hash);
}
