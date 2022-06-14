package ch.tobisyurt.comments.model;

public class Quiz {

    private String content;
    private Integer securityLevel;

    public Quiz(String content, int securityLevel) {
        this.content = content;
        this.securityLevel = securityLevel;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "content='" + content + '\'' +
                ", securityLevel=" + securityLevel +
                '}';
    }
}
