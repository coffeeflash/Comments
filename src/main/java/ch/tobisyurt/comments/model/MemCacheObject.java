package ch.tobisyurt.comments.model;

public class MemCacheObject {
    private Object value;
    private long expiryTime;

    public MemCacheObject(Object value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public Object getValue() {
        return value;
    }
}
