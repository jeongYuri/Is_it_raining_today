package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;
import org.springframework.stereotype.Repository;

import java.util.*;


public class MemoryUserRepository implements UserRepository{
    public static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setNo(++sequence);
        store.put(user.getNo(),user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return store.values().stream()
                .filter(user -> user.getId().equals(id))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}
