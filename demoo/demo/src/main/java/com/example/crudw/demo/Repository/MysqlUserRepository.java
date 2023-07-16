package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Member.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MysqlUserRepository implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    public MysqlUserRepository(DataSource dataSource) {

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public User save(User user) {
        String sql = "insert into users(no,id,pw,name,email,phone,create_time) values(null,?,?,?,?,?,'2023-06-24 17:50:00')";
        int result = jdbcTemplate.update(sql,user.getId(),user.getPw(),user.getName(),user.getEmail(),user.getPhone());
        log.warn("Hi I'm {MysqlUserRepository} log", "WARN");
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "delete from users where id=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<User> findById(Long no) {
        String sql = "select * from users where no =?";
        List<User> list = jdbcTemplate.query(sql,userRowMapper(),no);
        return list.stream().findAny();
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "select * from users where id =?";
        List<User> list = jdbcTemplate.query(sql,userRowMapper(),id);
        return list.stream().findAny();
    }

    @Override
    public User userUpdate(User user) {
        System.out.println(user);
        String sql = "update users set id=?,pw=?,name=?,email=?,phone=? where no=?";
        jdbcTemplate.update(sql,user.getId(),user.getPw(),user.getName(),user.getEmail(),user.getPhone(),user.getNo());
        System.out.println(user);
        return  user;
    }

    @Override
    public List<User>findAll(){

        return jdbcTemplate.query("select *from users",userRowMapper());
    }

    private RowMapper<User> userRowMapper() {
        return ((rs, rowNum) -> {
            User user = new User();
            user.setNo(rs.getLong("no"));
            user.setId(rs.getString("id"));
            user.setPw(rs.getString("pw"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            return user;
        });
    }
}
