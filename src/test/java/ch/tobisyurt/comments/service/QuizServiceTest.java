package ch.tobisyurt.comments.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        int nonceLength = 19;
        String quizContent = quizService.createQuiz("testId", securityLevel, 3600);
        System.out.println("CONTENT: " + quizContent);

        byte [] quizContentBytes = quizContent.getBytes(StandardCharsets.ISO_8859_1);

        SecureRandom rd = new SecureRandom();
        byte [] toHash = new byte[nonceLength + 8];
        do{
            //fills entire array with random bytes
            rd.nextBytes(toHash);
            // overwrite last byte with content
            for (int i = nonceLength; i < nonceLength+quizContentBytes.length ; i++) {
                toHash[i] = quizContentBytes[i-nonceLength];
            }
        }while(notAllZeroes(md.digest(toHash), securityLevel));

        System.out.println("Final Hash: " + quizService.toHexString(md.digest(toHash)));
        System.out.println("NONCE + CONTENT: " + quizService.toHexString(toHash));

        assertTrue(quizService.verifyQuizSolution("testId", new String(Arrays.copyOfRange(toHash, 0, nonceLength), StandardCharsets.ISO_8859_1)));
    }

    private boolean notAllZeroes(byte[] hash, int securityLevel){
        for (int i = 0; i < securityLevel; i++) {
            char[] binArr = new char[8];

            //if a bit is 1, emplace '1' at the respective position in the array, else 0
            for(int j = 0 ; j < 8 ; j++){
                if((hash[i] & (1 << j)) == 0){
                    binArr[7 - j] = '0';
                }else{
                    binArr[7 - j] = '1';
                    return true;
                }
            }
        }
        return false;
    }

}
