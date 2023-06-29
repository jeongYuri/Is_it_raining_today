package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Board.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
public class SqlBoardRepository implements BoardRepository{
    private final JdbcTemplate jdbcTemplate;
    private static long sequence = 0L;
    //private static Map<Long,Board> store = new HashMap<>();

    public SqlBoardRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Board insert(Board board) {
        board.setWriterNo(++sequence);
        //store.put(board.getWriterNo(),board);
        String sql = "insert into nboard(no,writer_no,writer_name,title,content,create_time,modify_time,hit,file_name,file_link) values(null,?,'udi',?,?,'2023-06-24 17:50:00','2023-06-26 17:50:00',?,?,?)";
        int result = jdbcTemplate.update(sql,board.getWriterNo(),board.getTitle(),board.getContent(),board.getHit(),board.getFileName(),board.getFileLink());
        return board;
    }

    @Override
    public List<Board> findAll() {
        return jdbcTemplate.query("select *from nboard",boardRowMapper());
    }

    private RowMapper<Board> boardRowMapper() {
        return ((rs, rowNum) -> {
            Board board = new Board();
            //user.setNo(rs.getLong("no"));
            board.setWriterNo(rs.getLong("writerno"));
            board.setWriterName(rs.getString("writername"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setFileName(rs.getString("filename"));
            board.setFileLink(rs.getNString("filelink"));
            return board;
        });
    }

    @Override
    public Optional<Board> findById(Long no) {
        return Optional.empty();
    }
}