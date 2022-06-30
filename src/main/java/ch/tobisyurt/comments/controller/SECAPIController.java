package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.CommentCategoryCount;
import ch.tobisyurt.comments.model.ReplyReq;
import ch.tobisyurt.comments.service.CommentsService;
import ch.tobisyurt.comments.service.SecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secapi")
public class SECAPIController {

    @Autowired
    private CommentsService commentsService;

    @Value("${admin.name}")
    private String admin;

    private static final Logger LOG = LoggerFactory.getLogger(SECAPIController.class);
    private static final String APISEC_MAPPING_POST_REPLY = "/reply";
    private static final String APISEC_MAPPING_POST_DELETE = "/delete";
    private static final String APISEC_MAPPING_GET_COMMENTS = "/comments";
    private static final String APISEC_MAPPING_GET_COMMENT_CATEGORIES = "/commentCategories";


    @PostMapping(value = APISEC_MAPPING_POST_REPLY)
    public void replyToComment(@RequestBody ReplyReq replyReq) {
        LOG.info("{} got called.", APISEC_MAPPING_POST_REPLY);
        commentsService.replyToComment(replyReq.getId(), this.admin, SecUtil.newLines(replyReq.getText()));
    }

    @GetMapping(value = APISEC_MAPPING_GET_COMMENTS)
    public List<Comment> getComments(@RequestParam String source) {
        LOG.info("{} got called.", APISEC_MAPPING_GET_COMMENTS);
        return commentsService.getComments(source, "");
    }

    @GetMapping(value = APISEC_MAPPING_GET_COMMENT_CATEGORIES)
    public List<CommentCategoryCount> getCommentCategoryCounts() {
        LOG.info("{} got called.", APISEC_MAPPING_GET_COMMENT_CATEGORIES);
        return commentsService.getCommentCategoryCounts();
    }

    @PostMapping(value = APISEC_MAPPING_POST_DELETE)
    public void deleteComment(@RequestParam String id) {
        LOG.info("{} got called wit id: {}", APISEC_MAPPING_POST_DELETE, id);
        commentsService.deleteComment(id);
    }

    // to ensure nothing gets out for security reasons
    // TODO introduce a more useful exception handling
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        LOG.error(e.getMessage());
        return new ResponseEntity("Either you try something nasty or you just have to read the api-docs again...", HttpStatus.CONFLICT);
    }

}
