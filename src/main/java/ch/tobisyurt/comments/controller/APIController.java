package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.Quiz;
import ch.tobisyurt.comments.service.CommentsService;
import ch.tobisyurt.comments.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CommentsService commentsService;

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String API_MAPPING_GET_COMMENTS = "/comments";
    private static final String API_MAPPING_GET_QUIZ = "/quiz";
    private static final String API_MAPPING_POST_QUIZ_SOLUTION = "/solution";
    private static final String API_MAPPING_POST_COMMENT = "/comment";

    @GetMapping(value = API_MAPPING_GET_QUIZ)
    public Quiz getQuiz(){
        return quizService.createQuiz( 2, 4, 60);
    }

    @GetMapping(value = API_MAPPING_GET_COMMENTS)
    public List<Comment> getComments(@RequestHeader(value =  HttpHeaders.REFERER) final String referer,
                                     @RequestParam String source) {

        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_GET_COMMENTS, referer, source);
        return commentsService.getComments(source, referer);
    }

    @PostMapping(value = API_MAPPING_POST_QUIZ_SOLUTION)
    public void verifyQuizSolution(@RequestParam List<String> nonceStrings, @RequestParam String quizId){
        boolean nonceValid = quizService.verifyQuizSolution(quizId, nonceStrings);
        LOG.info("nonceValid: {}", nonceValid);
    }

    @PostMapping(value = API_MAPPING_POST_COMMENT)
    public void addComment(@RequestHeader(value =  HttpHeaders.REFERER) final String referer, @RequestBody Comment comment){
        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_POST_COMMENT, referer, comment.getSource());
        boolean nonceValid = quizService.verifyQuizSolution(comment.getQuizId(), comment.getQuizSolutions());

        if(!nonceValid){
            LOG.info("Nonce is invalid rejected this request!");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        commentsService.addComment(comment, referer);
    }

    // to ensure nothing gets out for security reasons
    // TODO introduce a more useful exception handling
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleBlogAlreadyExistsException(Exception e) {
        LOG.error(e.getMessage());
        return new ResponseEntity("Upsiiii, das geit so niit!", HttpStatus.CONFLICT);
    }

}
