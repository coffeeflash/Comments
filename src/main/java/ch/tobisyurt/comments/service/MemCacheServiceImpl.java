package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.MemCacheObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemCacheServiceImpl implements MemCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(MemCacheServiceImpl.class);

    // TODO move to constructor and make it available over properties...
    private static final int CLEAN_UP_PERIOD_IN_SEC = 10;
    private static final int MILLI_TO_SEC_FACT = 1000;
    private final ConcurrentHashMap<String, SoftReference<MemCacheObject>> cache = new ConcurrentHashMap<>();

    public MemCacheServiceImpl() {
        LOG.info("QuizCache started");
        Thread cleanerThread = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * MILLI_TO_SEC_FACT);
                    cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
                            .map(MemCacheObject::isExpired).orElse(false));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

    @Override
    public void add(String key, Object value, long periodInSeconds) {
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.remove(key);
        } else {
            long expiryTime = System.currentTimeMillis() + periodInSeconds * MILLI_TO_SEC_FACT;
            cache.put(key, new SoftReference<>(new MemCacheObject(value, expiryTime)));
        }
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public Object get(String key) {
        return Optional.ofNullable(cache.get(key)).map(SoftReference::get).filter(cacheObject -> !cacheObject.isExpired())
                .map(MemCacheObject::getValue).orElse(null);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public long size() {
        return cache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
                .map(cacheObject -> !cacheObject.isExpired()).orElse(false)).count();
    }

    @Override
    public long realSize() {
        return cache.size();
    }
}
