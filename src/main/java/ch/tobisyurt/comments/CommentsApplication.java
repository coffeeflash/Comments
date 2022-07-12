package ch.tobisyurt.comments;

import ch.tobisyurt.comments.controller.APIController;
import ch.tobisyurt.comments.model.Comment;
import ch.tobisyurt.comments.repository.CommentsRepo;
import ch.tobisyurt.comments.service.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CommentsApplication {

	@Autowired
	private CommentsRepo commentsRepo;

	@Autowired
	private CommentsService commentsService;

	private static final Logger LOG = LoggerFactory.getLogger(CommentsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CommentsApplication.class, args);
	}

	@Bean
	SmartInitializingSingleton smartInitializingSingleton() {
		return () -> {
			if (commentsRepo.findAll().size() == 0) {
				LOG.info("Creating some example data, because the database is empty");
				for (int j = 1; j <= 3; j++) {
					for (int i = 1; i <= 3; i++) {
						Comment c = new Comment();
						if(i%2==0) c.setRead(false);
						else c.setRead(true);
						c.setComment("An example comment " + i + " for post " + j);
						c.setUser("User " + j + i);
						c.setSource("https://blog.example/post" + j);
						c.setSourceTitle("post title nr." + j);
						commentsService.addComment(c);
						if (i==2){
							c.setAdmin("big boss");
							c.setReply("boss replies something...<br>Bye!");
							commentsService.replyToComment(commentsService.getComments(c.getSource()).get(0).getId(),
									"Big Boss", "boss replies something...<br>Bye!");
						}
					}
				}
			}
		};
	}

}
