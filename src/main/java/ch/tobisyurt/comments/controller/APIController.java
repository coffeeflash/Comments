package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.CommentReq;
import ch.tobisyurt.comments.model.Quiz;
import ch.tobisyurt.comments.service.CommentsService;
import ch.tobisyurt.comments.service.MemCacheService;
import ch.tobisyurt.comments.service.QuizService;
import ch.tobisyurt.comments.service.SecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CommentsService commentsService;

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
    public Quiz getQuiz(@RequestHeader(value =  HttpHeaders.REFERER) final String referer){
        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_GET_QUIZ, referer);
        LOG.info("Quiz count: {}, quiz complexity: {}, quiz validity in seconds: {}", quizCount, quizComplexity, quizValidityInSeconds);
        return quizService.createQuiz( quizComplexity, quizCount, quizValidityInSeconds);

    }

    @GetMapping(value = API_MAPPING_GET_COMMENTS)
    public List<Comment> getComments(HttpServletRequest request,
                                     @RequestParam String source) {

        String referer = request.getHeader(HttpHeaders.REFERER);
        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_GET_COMMENTS, referer, source);
        if(referer==null) throw new RuntimeException("no referer in the headers...");
        return commentsService.getComments(source, referer);

    }

    @PostMapping(value = API_MAPPING_POST_QUIZ_SOLUTION)
    public void verifyQuizSolution(@RequestParam List<String> nonceStrings, @RequestParam String quizId){

        boolean nonceValid = quizService.verifyQuizSolution(quizId, nonceStrings);
        LOG.info("nonceValid: {}", nonceValid);

    }

    @PostMapping(value = API_MAPPING_POST_COMMENT)
    public String addComment(HttpServletRequest request, @RequestBody CommentReq commentReq){

        String referer = request.getHeader(HttpHeaders.REFERER);
        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_POST_COMMENT, referer, commentReq.getSource());
        LOG.info("Ip block time: {}", ipBlockTime);
        // somehow request.getRemoteUser() does not convert to a proper String... no hashCode...
        String clientIpAddress = "" + request.getRemoteUser();
        LOG.info("Client IP Address is: {}", request.getRemoteAddr());
        if(referer==null) throw new RuntimeException("no referer in the headers...");

        if(memCacheService.get(clientIpAddress) != null) {
            return "You can only post a comment every " + ipBlockTime / 60 + " minutes.";
        }

        if(!SecUtil.validate(commentReq.getComment())
                || !SecUtil.validate(commentReq.getUser())
                || !quizService.verifyQuizSolution(commentReq.getQuizId(), commentReq.getQuizSolutions())){

            LOG.info("rejected this request! (Either a nonce is invalid or you tried to bypass the input validation)");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }


        memCacheService.add(clientIpAddress, "waiting", ipBlockTime);
        commentReq.setComment(SecUtil.newLines(commentReq.getComment()));
        commentsService.addComment(commentReq, referer);

        return "ok";
    }

    // to ensure nothing gets out for security reasons
    // TODO introduce a more useful exception handling
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        LOG.error(e.getMessage());
        return new ResponseEntity("Either you try something nasty or you just have to read the api-docs again...", HttpStatus.CONFLICT);
    }

}
