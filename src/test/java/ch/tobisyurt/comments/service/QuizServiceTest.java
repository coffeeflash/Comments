package ch.tobisyurt.comments.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    MessageDigest md = MessageDigest.getInstance("SHA-256");

    public QuizServiceTest() throws NoSuchAlgorithmException {
    }

    @Test
    void createQuiz(){
        String quizContent = quizService.createQuiz("testId", 10, 60);
        System.out.println("CONTENT: " + quizContent);
    }

    @Test
    void createQuizAndVerify(){
        int securityLevel = 2;
        String quizContent = quizService.createQuiz("testId", securityLevel, 3600);
        System.out.println("CONTENT: " + quizContent);
        int i = 0;
        String toHash;

        do{
            i++;
            toHash = i + quizContent;
        }while(notAllZeroes(md.digest(toHash.getBytes()), securityLevel));

        System.out.println("Final Hash: " + quizService.toHexString(md.digest(toHash.getBytes())));
        System.out.println("NONCE + CONTENT: " + quizService.toHexString(toHash.getBytes()));

        assertTrue(quizService.verifyQuizSolution("testId", Integer.toString(i)));
    }

    private boolean notAllZeroes(byte[] hash, int securityLevel){
        for (int i = 0; i < securityLevel; i++) {
            //if a bit is 1, emplace '1' at the respective position in the array, else 0
            for(int j = 0 ; j < 8 ; j++){
                if((hash[i] & (1 << j)) != 0){
                    return true;
                }
            }
        }
        return false;
    }

}
