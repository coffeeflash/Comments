package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService{

    @Autowired
    private MemCacheService memCacheService;

    private static final Logger LOG = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final String abc = "abcdefghijklmnopqrstuvwxyz";
    // TODO a lot of invalid chars for request parameters, change again, if data moved to body!
//    private final String characters = "0123456789" + abc + abc.toUpperCase() + "{}[]()+-";
    private final String characters = "0123456789" + abc + abc.toUpperCase() + "";

    private final MessageDigest md;
    private final SecureRandom rd;

    public QuizServiceImpl() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-256");
        this.rd = new SecureRandom();
    }

    @Override
    public Quiz createQuiz(int securityLevel, int quizCount, int validityInSeconds) {
        List<String> quizStrings = new ArrayList<>();
        for (int i = 0; i < quizCount; i++) {
            quizStrings.add(generateString(32));
        }
        LOG.info("Quiz Strings: {}", quizStrings);
        Quiz q = new Quiz(quizStrings, securityLevel);
        // TODO ensure that there is at least 1 element in the list
        memCacheService.add(q.getContents().get(0), q, validityInSeconds);
        return q;
    }

    private String generateString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(this.characters.charAt(this.rd.nextInt(this.characters.length())));
        }
        return sb.toString();
    }

    /**
     * A Quiz can only be solved or tried to solve once. It gets deleted afterwards.
     * If one needs another try, a new quiz must be generated.
     *
     * @param quizId        identifies the quiz to which the solution belongs
     * @param nonceStrings  nonce's generating a hash with the # leading zeroes indicated by the securityLevel
     *
     * @retorn true for a correct solution...
      */
    @Override
    public boolean verifyQuizSolution(String quizId, List<String> nonceStrings) {
        // TODO ensure that there is at least 1 element in the list (in controller maybe...)
        Object quizToVerifyObj = memCacheService.get(quizId);

        if(quizToVerifyObj == null){
            LOG.info("Quiz with id: {} not found", quizId);
            return false;
        } else {
            Quiz quizToVerify = (Quiz) quizToVerifyObj;
            LOG.info("Verify Quiz with id: {} and suggested nonceStrings: {}", quizId, nonceStrings);

            List <String> quizContents = quizToVerify.getContents();
            if(quizContents.size() != nonceStrings.size()){
                LOG.info("Wrong number of nonceStrings");
                return false;
            }
            for (int i = 0; i < quizContents.size(); i++) {
                byte[] toHash = (nonceStrings.get(i) + quizToVerify.getContents().get(i)).getBytes();
                LOG.info("TO_HASH nr. {} (nonce + content): {}", i, toHexString(toHash));
                byte[] hashedQuiz = md.digest(toHash);
                LOG.info("Hash nr. {} looks like: {}", i, toHexString(hashedQuiz));
                if(!isHashValid(hashedQuiz, quizToVerify.getSecurityLevel())) {
                    memCacheService.remove(quizId);
                    return false;
                }
            }

            memCacheService.remove(quizId);
            return true;
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
