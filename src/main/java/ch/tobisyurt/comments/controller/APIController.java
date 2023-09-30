package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Quiz;
import ch.tobisyurt.comments.service.MemCacheService;
import ch.tobisyurt.comments.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private MemCacheService memCacheService;

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String API_MAPPING_GET_COMMENTS = "/comments";
    private static final String API_MAPPING_GET_QUIZ = "/quiz";
    private static final String API_MAPPING_POST_QUIZ_SOLUTION = "/solution";
    private static final String API_MAPPING_POST_COMMENT = "/comment";
    @Value("${quiz.count}")
    private int quizCount;
    @Value("${quiz.validity.seconds}")
    private int quizValidityInSeconds;
    @Value("${quiz.complexity}")
    private int quizComplexity;
    @Value("${ip.block.time}")
    private int ipBlockTime;

    @GetMapping(value = API_MAPPING_GET_QUIZ)
    public Quiz getQuiz(){
        LOG.info("Quiz count: {}, quiz complexity: {}, quiz validity in seconds: {}", quizCount, quizComplexity, quizValidityInSeconds);
        return quizService.createQuiz( quizComplexity, quizCount, quizValidityInSeconds);

    }

    @GetMapping(value = "/load")
    public List<Integer> getLoad(@RequestParam Integer count) {
        LOG.info("{} got called for post: {}", "/load",count);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        if(list.size() > 10000) {
            return new ArrayList<Integer>(100);

        }

        return list;
    }


    @PostMapping(value = API_MAPPING_POST_QUIZ_SOLUTION)
    public void verifyQuizSolution(@RequestParam List<String> nonceStrings, @RequestParam String quizId){

        boolean nonceValid = quizService.verifyQuizSolution(quizId, nonceStrings);
        LOG.info("nonceValid: {}", nonceValid);

    }

    // to ensure nothing gets out for security reasons
    // TODO introduce a more useful exception handling
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        LOG.error(e.getMessage());
        return new ResponseEntity("Either you try something nasty or you just have to read the api-docs again...", HttpStatus.CONFLICT);
    }

}
