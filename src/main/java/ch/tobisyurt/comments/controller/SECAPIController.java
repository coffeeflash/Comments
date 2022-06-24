package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.CommentCategoryCount;
import ch.tobisyurt.comments.service.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secapi")
public class SECAPIController {

    @Autowired
    private CommentsService commentsService;

    private static final Logger LOG = LoggerFactory.getLogger(SECAPIController.class);
    private static final String APISEC_MAPPING_POST_REPLY = "/reply";
    private static final String APISEC_MAPPING_POST_DELETE = "/delete";
    private static final String APISEC_MAPPING_GET_COMMENTS = "/comments";
    private static final String APISEC_MAPPING_GET_COMMENT_CATEGORIES = "/commentCategories";


    @GetMapping(value = APISEC_MAPPING_POST_REPLY)
    public void replyToComment(@RequestParam String source) {
        LOG.info("{} got called.", APISEC_MAPPING_POST_REPLY);

    }

    @GetMapping(value = APISEC_MAPPING_GET_COMMENT_CATEGORIES)
    public List<CommentCategoryCount> getCommentCategoryCounts() {
        LOG.info("{} got called.", APISEC_MAPPING_GET_COMMENT_CATEGORIES);
        return commentsService.getCommentCategoryCounts();
    }

    // to ensure nothing gets out for security reasons
    // TODO introduce a more useful exception handling
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        LOG.error(e.getMessage());
        return new ResponseEntity("Either you try something nasty or you just have to read the api-docs again...", HttpStatus.CONFLICT);
    }

}
