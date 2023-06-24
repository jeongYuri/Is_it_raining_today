package com.example.crudw.demo.Service;

import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Slf4j
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User join(User user){
        validateDuplicateUser(user); //중복 회원 검증
        userRepository.save(user);
        log.warn("Hi I'm {join} log", "WARN");
        return user;
    }

    private void validateDuplicateUser(User user) {
        userRepository.findById(user.getId())
                .ifPresent(u->{
                    throw new IllegalStateException("이미 존재하는 회원임");
                });
    }
}
