package com.example.crudw.demo.Service;

import com.example.crudw.demo.Repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    private final DataSource dataSource;
    public AppConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Bean
    public UserService userService(){
        return new UserService(userRepository());
    }
    @Bean
    public BoardService boardService(){return new BoardService(boardRepository());}
    @Bean
    public CommentService commentService(){return new CommentService(commentRepository());}
    @Bean
    public CommentRepository commentRepository(){return new SqlCommentRepository(dataSource);}
    @Bean
    public BoardRepository boardRepository() {return new SqlBoardRepository(dataSource);}
    @Bean
    public UserRepository userRepository() {
        return new MysqlUserRepository(dataSource);
    }
}
