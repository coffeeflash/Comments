package ch.tobisyurt.comments.model;

public class CommentCategoryCount {
    private String source;
    private String sourceTitle;
    private long count;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CommentCategoryCount{" +
                "source='" + source + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", count=" + count +
                '}';
    }
}
