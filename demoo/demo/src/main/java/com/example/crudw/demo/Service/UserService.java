package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.Heart.Heart;
import com.example.crudw.demo.Heart.HeartRepository;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Member.UserRepository;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final HeartRepository heartRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BoardRepository boardRepository, CommentRepository commentRepository, HeartRepository heartRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.heartRepository = heartRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));
        // 평문 비밀번호를 가진 기존 사용자 처리를 위한 임시 로직
        // passwordEncoder.matches()는 평문과 암호화된 비밀번호를 모두 처리합니다.
        // 하지만 기존 평문 비밀번호를 암호화하여 업데이트하기 위해 명시적으로 검증합니다.
        if (user.getPw() != null && !user.getPw().startsWith("$2a$")) {
            // 암호화되지 않은(평문) 비밀번호인 경우, 새로운 암호화된 비밀번호로 업데이트합니다.
            String encodedPassword = passwordEncoder.encode(user.getPw());
            user.setPw(encodedPassword);
            userRepository.save(user); // DB에 업데이트된 비밀번호 저장
        }
        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPw(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional // 트랜잭션 보장
    public void save(User user){
        // 일반 회원가입 시 registrationId가 null로 넘어오는 것을 방지
        if (user.getRegistrationId() == null) {
            user.setRegistrationId("none");
        }
        user.setPw(passwordEncoder.encode(user.getPw()));
        userRepository.save(user);
    }


    @Transactional
    public void deleteUser(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            List<Board> boardList = boardRepository.findByWriterName(user.getId());

            for (Board board : boardList) {
                List<Heart> hearts = heartRepository.findByBoardId(board.getNo());
                heartRepository.deleteAll(hearts);
                commentRepository.deleteByBoardNo(board.getNo());
                boardRepository.deleteById(board.getNo());

            }

            List<Comment> commentList = commentRepository.findByWriterName(user.getId());
            for (Comment comment : commentList) {
                if(comment.getParentNo()==null){
                    deleteChildComments(comment);
                }
                commentRepository.deleteByWriterName(comment.getWriterName());
            }
        }
        userRepository.deleteById(id);
    }
    @Transactional
    public void deleteSocialUser(String name) {
        Optional<User> userOptional = userRepository.findByName(name);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            List<Board> boardList = boardRepository.findByWriterName(user.getName());

            for (Board board : boardList) {
                List<Heart> hearts = heartRepository.findByBoardId(board.getNo());
                heartRepository.deleteAll(hearts);
                commentRepository.deleteByBoardNo(board.getNo());
                boardRepository.deleteById(board.getNo());
            }
            List<Comment> commentList = commentRepository.findByWriterName(user.getName());
            for (Comment comment : commentList) {
                if(comment.getParentNo()==null){
                    deleteChildComments(comment);
                }
                commentRepository.deleteByWriterName(comment.getWriterName());
            }
        }
        userRepository.deleteByName(name);
    }
    private void deleteChildComments(Comment comment) {
        List<Comment> childComments = commentRepository.findByParentNo(comment.getParentNo());
        for (Comment childComment : childComments) {
            deleteChildComments(childComment);
            commentRepository.deleteByWriterName(childComment.getWriterName());
        }
    }

    public User getUser(String id){
        return userRepository.findById(id).orElse(null);
    }
    public User getUserName(String name){
        return userRepository.findByName(name).orElse(null);
    }
    public User getUser(Long no) {
        Optional<User> userOpt = userRepository.findById(no);
        return userOpt.orElse(null);
    }
    public User findId(String name, String email){
        return userRepository.findByNameAndEmail(name, email).orElse(null);
    }
    @Transactional
    public User updateUser(String id, UserUpdate updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));

        if (updateDTO.getId() != null) {
            user.setId(updateDTO.getId());
        }
        if (updateDTO.getPw() != null) {
            user.setPw(passwordEncoder.encode(updateDTO.getPw()));
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
    @Transactional
    public User updatesocialUser(String name, UserUpdate updateDTO) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + name));

        if (updateDTO.getId() == null) {
            user.setId(updateDTO.getId());
        }
        if (updateDTO.getPw() == null) {
            user.setPw(passwordEncoder.encode(updateDTO.getPw()));
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
    public void socialLogin(String code, String registrationId) {
        System.out.println("code = " + code);
        System.out.println("registrationId = " + registrationId);
    }
}
