package org.tohant.financesweb.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class CacheRepository implements IRepository<String, Object> {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public void save(String key, Object value) {
        log.debug("Saving an object to cache repository: " + key);
        CACHE.put(key, value);
    }

    public Object get(String key) {
        log.debug("Retrieving from cache repository: " + key);
        return CACHE.get(key);
    }

}
