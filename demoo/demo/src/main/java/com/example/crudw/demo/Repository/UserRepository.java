package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;


import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    void deleteById(String id);
   Optional<User> findById(Long no);
    Optional<User> findById(String id);
    User userUpdate(User user);
    List<User> findAll();


}
