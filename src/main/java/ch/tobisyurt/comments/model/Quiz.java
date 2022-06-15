package ch.tobisyurt.comments.model;

import java.util.List;

public class Quiz {

    private List<String> contents;
    private Integer securityLevel;

    public Quiz(List<String> contents, int securityLevel) {
        this.contents = contents;
        this.securityLevel = securityLevel;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public List<String> getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "contents='" + contents + '\'' +
                ", securityLevel=" + securityLevel +
                '}';
    }
}
