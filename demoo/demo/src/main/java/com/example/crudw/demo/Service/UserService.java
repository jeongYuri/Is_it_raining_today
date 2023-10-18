package com.example.crudw.demo.Service;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.Heart.Heart;
import com.example.crudw.demo.Heart.HeartRepository;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Member.UserUpdate;
import com.example.crudw.demo.Member.UserRepository;
import com.example.crudw.demo.Notification.EmitterRepository;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentRepository;
import com.example.crudw.demo.comment.CommentRequestDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HeartRepository heartRepository;

    //public UserService(UserRepository userRepository) {
        //this.userRepository = userRepository;
    //}
    @Transactional
    public void deleteUser(String id) {
        Optional<User> userOptional = userRepository.findUserById(id); //user를 찾을수도 있고 없을수도 있으니까~!!
        if(userOptional.isPresent()){ //유저가 있다면
            User user = userOptional.get();
            List<Board> boardList = boardRepository.findByWriterName(user.getId());

            for (Board board : boardList) {
                List<Heart> hearts = heartRepository.findByBoardNo(board.getNo());
                heartRepository.deleteAll(hearts); //게시글 좋아요 다 삭제
                commentRepository.deleteByBoardNo(board.getNo()); //댓글 삭제
                boardRepository.deleteById(board.getNo()); //게시글 삭제

            }

            List<Comment> commentList = commentRepository.findByWriterName(user.getId());
            for (Comment comment : commentList) {
                if(comment.getParentNo()==null){//부모 자식 관계 끊기
                    deleteChildComments(comment); //자식 댓글 삭제
                }
                commentRepository.deleteByWriterName(comment.getWriterName());
            }
        }
           userRepository.deleteById(id);
        }
    @Transactional
    public void deleteSocialUser(String name) {
        Optional<User> userOptional = userRepository.findUserByName(name); //user를 찾을수도 있고 없을수도 있으니까~!!
        if(userOptional.isPresent()){ //유저가 있다면
            User user = userOptional.get();
            List<Board> boardList = boardRepository.findByWriterName(user.getName());

            for (Board board : boardList) {
                List<Heart> hearts = heartRepository.findByBoardNo(board.getNo());
                heartRepository.deleteAll(hearts); //게시글 좋아요 다 삭제
                commentRepository.deleteByBoardNo(board.getNo()); //댓글 삭제
                boardRepository.deleteById(board.getNo()); //게시글 삭제

            }
            List<Comment> commentList = commentRepository.findByWriterName(user.getName());
            for (Comment comment : commentList) {
                if(comment.getParentNo()==null){//부모 자식 관계 끊기
                    deleteChildComments(comment); //자식 댓글 삭제
                }
                commentRepository.deleteByWriterName(comment.getWriterName());
            }
        }
        userRepository.deleteByName(name);
    }
    private void deleteChildComments(Comment comment) {     // 부모 댓글에 속한 아이 댓글 삭제
        List<Comment> childComments = commentRepository.findByParentNo(comment.getParentNo());
        for (Comment childComment : childComments) {
            deleteChildComments(childComment);
            commentRepository.deleteByWriterName(childComment.getWriterName());
        }
    }

    public User getUser(String id){
        User user = userRepository.findById(id);

        return user;
    }
    public User getUserName(String name){
        User user = userRepository.findByName(name);
        return user;
    }
    public Long saveUser(User user) {
        return userRepository.save(user).getNo();
    }

    public User getUser(Long no) {
        Optional<User> userOpt = userRepository.findById(no);
        return userOpt.orElse(null);
    }


    public boolean login(String id, String pw) {
        User user = getUser(id);
        if (user == null) {
            return false;
        }
        String storedPw = user.getPw();
        boolean result = pw.equals(storedPw);

        return result;
    }
    public User findId(String name, String email){
        User founduser = userRepository.findByNameAndEmail(name, email);
        return founduser;
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
    @Transactional
    public User updatesocialUser(String name, UserUpdate updateDTO) {
        User user = userRepository.findByName(name);

        if (updateDTO.getId() == null) {
            user.setId(updateDTO.getId());
        }
        if (updateDTO.getPw() == null) {
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
    public void socialLogin(String code, String registrationId) {
        System.out.println("code = " + code);
        System.out.println("registrationId = " + registrationId);
    }

}
