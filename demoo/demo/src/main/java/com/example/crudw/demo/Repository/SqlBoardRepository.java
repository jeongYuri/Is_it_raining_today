package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Board.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
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
        board.setWriter_no(++sequence);
        board.setCreate_time(LocalDateTime.now()); // 현재 시간으로 설정
        board.setModify_time(LocalDateTime.now());
        //store.put(board.getWriterNo(),board);
        String sql = "insert into nboard(no,writer_no,writer_name,title,content,create_time,modify_time,hit,file_name,file_link) values(?,?,?,?,?,?,?,?,?,?)";
        int result = jdbcTemplate.update(sql, board.getNo(),board.getWriter_no(),board.getWriter_name(), board.getTitle(), board.getContent(), board.getCreate_time(), board.getModify_time(), board.getHit(), board.getFile_name(), board.getFile_link());
        System.out.println(board);
        return board;
    }
    @Override
    public Board boardupdate(Board board){
        board.setModify_time(LocalDateTime.now());
        String sql = "UPDATE nboard SET title = ?, content = ?, modify_time=? WHERE no = ?";
        jdbcTemplate.update(sql, board.getTitle(), board.getContent(),board.getModify_time(), board.getNo());

         return board;
    }

    @Override
    public void hit(Long no) {
        String sql="UPDATE nboard SET hit = hit+1 WHERE no = ?";
        jdbcTemplate.update(sql,no);
    }

    @Override
    public void deleteById(Long no) {
        String sql ="delete from nboard where no = ?";
        int result = jdbcTemplate.update(sql,no);
    }


    @Override
    public List<Board> findAll() {
        return jdbcTemplate.query("select *from nboard",boardRowMapper());
    }

    private RowMapper<Board> boardRowMapper() {
        return ((rs, rowNum) -> {
            Board board = new Board();
            board.setNo(rs.getLong("no"));
            board.setWriter_no(rs.getLong("writer_no"));
            board.setWriter_name(rs.getString("writer_name"));
            board.setCreate_time(rs.getObject("create_time", LocalDateTime.class));
            board.setModify_time(rs.getObject("modify_time", LocalDateTime.class));
            board.setTitle(rs.getString("title"));
            board.setHit(rs.getInt("hit"));
            board.setContent(rs.getString("content"));
            board.setFile_name(rs.getString("file_name"));
            board.setFile_link(rs.getString("file_link"));
            return board;
        });
    }
    @Override
    public Optional<Board> findById(Long no) {
        String sql = "select * from nboard where no =?";
        List<Board> list = jdbcTemplate.query(sql,boardRowMapper(),no);
        return list.stream().findAny();
    }

    @Override
    public List<Board> findByWriterName(String writerName) {
        String sql = "select * from nboard where writer_name =?";
        return jdbcTemplate.query(sql,boardRowMapper(),writerName);
    }


}