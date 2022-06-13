package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class QuizServiceImpl implements QuizService{

    @Autowired
    private MemCacheService memCacheService;

    private static final Logger LOG = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final String abc = "abcdefghijklmnopqrstuvwxyz";
    private final String characters = "0123456789" + abc + abc.toUpperCase() + "{}[]()+-/%&";

    private final MessageDigest md;
    private final SecureRandom rd;

    public QuizServiceImpl() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-256");
        this.rd = new SecureRandom();
    }

    @Override
    public String createQuiz(String quizKey, int securityLevel, int validityInSeconds) {
        String quizString = generateString(32);
        LOG.info("Quiz bytes quizString: {}", quizString);
        Quiz q = new Quiz(quizString, securityLevel);
        memCacheService.add(quizKey, q, validityInSeconds);

        return quizString;
    }

    private String generateString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(this.characters.charAt(this.rd.nextInt(this.characters.length())));
        }
        return sb.toString();
    }

    @Override
    public boolean verifyQuizSolution(String quizId, String nonceString) {
        Object quizToVerifyObj = memCacheService.get(quizId);

        if(quizToVerifyObj == null){
            LOG.info("Quiz with id: {} not found", quizId);
            return false;
        } else {
            Quiz quizToVerify = (Quiz) quizToVerifyObj;
            LOG.info("Verify Quiz with id: {} and suggested nonce: {}", quizId, nonceString);
            byte[] toHash = (nonceString + quizToVerify.getContent()).getBytes();
            LOG.info("TO_HASH (nonce + content): {}", toHexString(toHash));

            byte[] hashedQuiz = md.digest(toHash);
            LOG.info("Hash looks like: " + toHexString(hashedQuiz));

            return isHashValid(hashedQuiz, quizToVerify.getSecurityLevel());
        }
    }

    private boolean isHashValid(byte[] hash, int securityLevel){
        for (int i = 0; i < securityLevel; i++) {
            for(int j = 0 ; j < 8 ; j++){
                if((hash[i] & (1 << j)) != 0){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toHexString(byte[] hash)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
