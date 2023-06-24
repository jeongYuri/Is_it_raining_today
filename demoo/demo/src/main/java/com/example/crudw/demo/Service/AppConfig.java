package com.example.crudw.demo.Service;

import com.example.crudw.demo.Repository.MysqlUserRepository;
import com.example.crudw.demo.Repository.UserRepository;
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
    public UserRepository userRepository() {
        return new MysqlUserRepository(dataSource);
    }
}
