package ch.tobisyurt.comments.controller;

import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String API_MAPPING_COMMENTS = "/comments";

    @GetMapping(value = API_MAPPING_COMMENTS)
    public List<Comment> getSemantics(@RequestHeader(value =  HttpHeaders.REFERER) final String referer, @RequestParam String post) {

        LOG.info("{} got called from referer: {} for post: {}", API_MAPPING_COMMENTS, referer, post);

        User u = new User();
        u.setEmail("tobi@tobisyurt.net");
        u.setPassword("abc");
        u.setUsername("Toubi Van Kenoubi");

        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Comment c = new Comment();
            c.setDate(new Date());
            c.setSource(post);
            c.setUser(u);
            c.setComment("this is comment nr. " + i);
            comments.add(c);
        }

        return comments;
    }


}
