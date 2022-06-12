package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.Quiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class QuizService {

    private static final Logger LOG = LoggerFactory.getLogger(QuizService.class);

    private final int MAX_ATTEMPT = 10;

    private Map<Date, Quiz> cache = new TreeMap();

    public void put(String quiz){
        if(cache.containsKey(quiz)){
            LOG.info("conatins key: " + quiz);
            Integer oldValue = cache.get(quiz);
            cache.replace(quiz, oldValue.intValue()+1);
        }else{
            cache.put(quiz, 1);
        }
        printMap();
    }

    private void printMap(){
        LOG.info(cache.toString());
    }




}
