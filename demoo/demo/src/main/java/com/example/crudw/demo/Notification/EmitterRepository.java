package com.example.crudw.demo.Notification;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
    public final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();
    public Map<String, SseEmitter> findAllByIdStartingWith(String id) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void saveEventCache(String id, Object event) {
        eventCache.put(id, event);
    }

    public void deleteById(String id) {
        emitters.remove(id);
    }

    public Map<String, Object> findAllEventCacheStartWithId(String id) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SseEmitter save(String id, SseEmitter sseEmitter) {
        emitters.put(id, sseEmitter);
        return sseEmitter;
    }
    public void deleteAllEventCacheStartWithId(String id) {
        eventCache.forEach(
                (key, data) -> {
                    if (key.startsWith(id)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}