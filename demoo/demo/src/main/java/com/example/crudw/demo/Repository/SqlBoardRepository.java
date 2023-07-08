package com.example.crudw.demo.Repository;

import com.example.crudw.demo.Board.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
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
        //store.put(board.getWriterNo(),board);
        String sql = "insert into nboard(no,writer_no,writer_name,title,content,create_time,modify_time,hit,file_name,file_link) values(?,?,'udi',?,?,'2023-06-24 17:50:00','2023-06-26 17:50:00',?,?,?)";
        int result = jdbcTemplate.update(sql,board.getNo(),board.getWriter_no(),board.getTitle(),board.getContent(),board.getHit(),board.getFile_name(),board.getFile_link());
        System.out.println(board);
        return board;
    }
    @Override
    public Board boardupdate(Board board){
        System.out.println(board);
        String sql = "UPDATE nboard SET title = ?, content = ? WHERE no = ?";
        jdbcTemplate.update(sql, board.getTitle(), board.getContent(), board.getNo());

        //    System.out.println(board);
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
            board.setTitle(rs.getString("title"));
            board.setHit(rs.getInt("hit"));
            board.setContent(rs.getString("content"));
            board.setFile_name(rs.getString("file_name"));
            board.setFile_link(rs.getNString("file_link"));
            return board;
        });
    }
    @Override
    public Optional<Board> findById(Long no) {
        String sql = "select * from nboard where no =?";
        List<Board> list = jdbcTemplate.query(sql,boardRowMapper(),no);
        return list.stream().findAny();
    }


}