package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private MemCacheService memCacheService;

    private MessageDigest md = MessageDigest.getInstance("SHA-256");

    public QuizServiceTest() throws NoSuchAlgorithmException {
    }


    @Test
    void createQuiz(){
        Quiz q = quizService.createQuiz(1,20);
        assertNotNull(q);
        assertNotNull(memCacheService.get(q.getContent()));
        assertNull(memCacheService.get("none existing key..."));
    }

    @Test
    void createQuizAndVerify(){
        int securityLevel = 2;
        Quiz q = quizService.createQuiz(securityLevel, 3600);
        int nonce = 0;
        String toHash;
        do{
            nonce++;
            toHash = nonce + q.getContent();
        }while(notAllZeroes(md.digest(toHash.getBytes()), securityLevel));

        System.out.println("FINAL HASH: " + quizService.toHexString(md.digest(toHash.getBytes())));
        System.out.println("NONCE + CONTENT: " + quizService.toHexString(toHash.getBytes()));

        assertTrue(quizService.verifyQuizSolution(q.getContent(), Integer.toString(nonce)));
    }

    private boolean notAllZeroes(byte[] hash, int securityLevel){
        for (int i = 0; i < securityLevel; i++) {
            for(int j = 0 ; j < 8 ; j++){
                if((hash[i] & (1 << j)) != 0){
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    void memCacheTest() throws InterruptedException {
        System.out.println("Size of memCache: " + memCacheService.size());

        int valididyInSeconds = 1;
        int quizCount = 100;
        for (int i = 0; i < quizCount; i++) {
            quizService.createQuiz(2,valididyInSeconds);
        }

        assertTrue(memCacheService.size() >= quizCount);
        System.out.println("Size of memCache after adding 100 quizzes: " + memCacheService.size());
        System.out.println("Real size of memCache after adding 100 quizzes: " + memCacheService.realSize());
        Thread.sleep(valididyInSeconds * 1000 * 2); // *2 to make sure that the cleaner thread has done his work
        System.out.println("Size of memCache after waiting: " + memCacheService.size());
        System.out.println("Real size of memCache after waiting: " + memCacheService.realSize());
        assertTrue(memCacheService.size() < quizCount);
    }

}
