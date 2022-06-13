package ch.tobisyurt.comments.model;

import java.util.Date;

public class Quiz {

    private String content;
    private Integer attempts;
    private Integer securityLevel;

    public Quiz(String content, int securityLevel) {
        this.content = content;
        this.securityLevel = securityLevel;
        this.attempts = 0;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public String getContent() {
        return content;
    }

    public Integer getAttempts() {
        return attempts;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "content='" + content + '\'' +
                ", securityLevel=" + securityLevel +
                '}';
    }
}
