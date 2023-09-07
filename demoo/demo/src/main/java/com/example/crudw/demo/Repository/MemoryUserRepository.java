package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;

import java.util.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


public class MemoryUserRepository{
    public static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;


    public User save(User user) {
        user.setNo(++sequence);
        store.put(user.getNo(),user);
        return user;
    }


    public void deleteById(String id) {

    }


    public Optional<User> findById(Long no) {
        return Optional.empty();
    }


    public Optional<User> findById(String id) {
        return store.values().stream()
                .filter(user -> user.getId().equals(id))
                .findAny();
    }


    public User userUpdate(User user) {
        return null;
    }



    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}
