package com.example.crudw.demo.Member;

import com.example.crudw.demo.Member.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    void deleteById(String id);
    //User findById(Long no);
    User findById(String id);
    Optional<User> findUserById(String id);
}
