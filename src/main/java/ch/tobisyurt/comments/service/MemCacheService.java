package ch.tobisyurt.comments.service;

public interface MemCacheService {
    void add(String key, Object value, long periodInSeconds);

    void remove(String key);

    Object get(String key);

    void clear();

    long size();
}
