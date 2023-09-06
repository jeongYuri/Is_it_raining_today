package com.example.crudw.demo.Service;

import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Member.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    //public UserService(UserRepository userRepository) {
        //this.userRepository = userRepository;
    //}

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    //public Long userUpdate(User user){return userRepository.userUpdate(user).getNo();}
    public User getUser(String id){
        User user = userRepository.findById(id);

        return user;
    }
    public Long saveUser(User user) {
        System.out.println(user.getId() + " " + user.getPw());
        System.out.println(user.getId() + " " + user.getPw());

        return userRepository.save(user).getNo();
    }

    public User getUser(Long no) {
        Optional<User> userOpt = userRepository.findById(no);
        return userOpt.orElse(null);
    }

    public boolean login(String id, String pw) {
        System.out.println(id + " " + pw);

        User user = getUser(id);

        if (user == null) {
            return false;
        }

        String storedPw = user.getPw();

        boolean result = pw.equals(storedPw);

        return result;
    }

    @Transactional
    public User updateUser(String id, UserUpdate updateDTO) {
        User user = userRepository.findById(id);


        if (updateDTO.getId() != null) {
            user.setId(updateDTO.getId());
        }
        if (updateDTO.getPw() != null) {
            user.setPw(updateDTO.getPw());
        }
        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }

        return userRepository.save(user);
    }

}
