package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
public class CommentServiceTest {

    @Value("${quiz.count}")
    private int quizCount;

    // Profiles test
    @Test
    void createQuiz(){
        System.out.println("QUIZ COUNT " + quizCount);
        assertTrue(quizCount == 1);
    }

}
