package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
}
