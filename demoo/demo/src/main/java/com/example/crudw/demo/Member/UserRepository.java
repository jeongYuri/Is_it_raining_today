package com.example.crudw.demo.Member;

import com.example.crudw.demo.Member.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    void deleteById(String id);
    void deleteByName(String name);
    //User findById(Long no);
    Optional<User> findById(String id);
    Optional<User> findByName(String name);
    Optional<User> findByNameAndEmail(String name, String email);
    Optional<User> findByEmail(String email);
}
