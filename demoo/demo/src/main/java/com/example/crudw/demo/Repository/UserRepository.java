package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;
import org.springframework.data.jpa.repository.JpaRepository;




public interface UserRepository extends JpaRepository<User,Long> {
    void deleteById(String id);
    //User findById(Long no);
    User findById(String id);



}
