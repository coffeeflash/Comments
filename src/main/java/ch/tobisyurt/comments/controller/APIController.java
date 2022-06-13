package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.repository.CommentsRepo;
import ch.tobisyurt.comments.service.MemCacheService;
import ch.tobisyurt.comments.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    @Autowired
    private CommentsRepo commentsRepo;

//    @Autowired
//    private MemCacheService quizService;

    @Autowired
    private QuizService quizService;

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String API_MAPPING_GET_COMMENTS = "/comments";
    private static final String API_MAPPING_GET_QUIZ = "/quiz";
    private static final String API_MAPPING_POST_QUIZ_SOLUTION = "/solution";

    @GetMapping(value = API_MAPPING_GET_QUIZ)
    public String getQuiz(){
        return quizService.createQuiz("lala", 2, 60);
    }

    @GetMapping(value = API_MAPPING_GET_COMMENTS)
    public List<Comment> getComments(@RequestHeader(value =  HttpHeaders.REFERER) final String referer,
                                     @RequestParam String post) {



        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_GET_COMMENTS, referer, post);

//
//        Quiz quiz = new Quiz("teststring", 8);
//
//        if(quizService.get("lalala") != null){
//            Quiz quizToGet = (Quiz) quizService.get("lalala");
//            LOG.info("Found a quiz: " + quizToGet.getContent());
//
//        }
//
//        quizService.add("lalala", quiz, 20);

     /*   User u = new User();
        u.setEmail("tobi@tobisyurt.net");
        u.setPassword("abc");
        u.setUsername("Toubi Van Kenoubi");

*/



        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Comment c = new Comment();
            c.setDate(new Date());
            c.setSource(post);
           /* c.setUser(u);*/
            c.setComment("this is comment nr. " + i);
            comments.add(c);
        }



        return comments;
    }

    @PostMapping(value = API_MAPPING_POST_QUIZ_SOLUTION)
    public void verifyQuizSolution(@RequestParam String nonce){
        boolean nonceValid = quizService.verifyQuizSolution("lala", nonce);
        LOG.info("nonceValid: {}", nonceValid);

    }


}
