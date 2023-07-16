package com.example.crudw.demo.Service;

import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

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
        userRepository.findById(user.getId()).ifPresent(u->{
                    throw new IllegalStateException("이미 존재하는 회원임");
                });
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    public Long userUpdate(User user){return userRepository.userUpdate(user).getNo();}
    public User getUser(String id){
        Optional<User> userO = userRepository.findById(id);
        System.out.println(userO);
        if (userO.isPresent()) {
            return userO.get();
        }
        return null;
    }

    public User getUser(Long no) {
        Optional<User> userOpt = userRepository.findById(no);
        User user = userOpt.get();
        return user;
    }
    public User login(String id, String pw){
        Optional<User> findUser = userRepository.findById(id);
        return findUser.filter(user->user.getPw().equals(pw))
                .orElse(null);
    }

}
