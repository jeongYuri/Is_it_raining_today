package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;

import java.util.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


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
    public void deleteById(String id) {

    }

    @Override
    public Optional<User> findById(Long no) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        return store.values().stream()
                .filter(user -> user.getId().equals(id))
                .findAny();
    }

    @Override
    public User userUpdate(User user) {
        return null;
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}
