package com.example.crudw.demo.Repository;
/*
import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.comment.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class SqlCommentRepository  {
    private final JdbcTemplate jdbcTemplate;
    private static long sequence = 0L;
    public SqlCommentRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public Comment save(Comment comment) {
        System.out.println("hi");
        comment.setWriter_no(++sequence);
        comment.setCreate_time(LocalDateTime.now());
        comment.setModify_time(LocalDateTime.now());
        System.out.println("hi");
        String sql = "insert into comment(no,board_no,writer_no,writer_name,content,create_time,modify_time) values(?,?,?,?,?,?,?)";
        int result = jdbcTemplate.update(sql, comment.getNo(),comment.getBoard_no(),comment.getWriter_no(),comment.getWriter_name(),comment.getContent(),comment.getCreate_time(),comment.getModify_time());
        System.out.println("complete");
        return comment;
    }


    public Comment commentupdate(Comment comment) {
        comment.setModify_time(LocalDateTime.now());
        String sql = "UPDATE comment SET content=?,modify_time=? WHERE no=?";
        jdbcTemplate.update(sql, comment.getContent(),comment.getModify_time(),comment.getNo());
        return comment;
    }


    public void deleteById(Long no) {
        String sql = "delete from comment where no=?";
        int result= jdbcTemplate.update(sql, no);

    }


    public void deleteByUserId(String writerName) {
        String sql = "delete from comment where writer_name=?";
        int result= jdbcTemplate.update(sql, writerName);
    }
/*
    @Override
    public List<Comment> findById(String writerName) {
        String sql = "select *from comment where writer_name=?";
        return jdbcTemplate.query(sql,commentRowMapper(), writerName);
    }


//    public List<Comment> findByBoardNoOrderByParentNoAscNoAsc(Long board_no) {
  //      return jdbcTemplate.query("SELECT * FROM comment WHERE board_no =? ORDER BY parent_no ASC, no ASC",commentRowMapper(),board_no);

    //}

    public List<Comment> findAll() {
        return jdbcTemplate.query("select *from comment",commentRowMapper());
    }

    private RowMapper<Comment>commentRowMapper() {
        return ((rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setNo(rs.getLong("no"));
            comment.setWriter_no(rs.getLong("writer_no"));
            comment.setWriter_name(rs.getString("writer_name"));
            comment.setContent(rs.getString("content"));
            comment.setBoard_no(rs.getLong("board_no"));
            comment.setCreate_time(rs.getObject("create_time", LocalDateTime.class));
            comment.setModify_time(rs.getObject("modify_time", LocalDateTime.class));
            return comment;
        });
    }


    public Optional<Comment> findByNo(Long no) {
        String sql = "select * from comment where no=?";
        List<Comment> list = jdbcTemplate.query(sql,commentRowMapper(),no);
        return list.stream().findAny();
    }
}
*/