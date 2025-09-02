package com.example.crudw.demo.Controller;


import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Heart.Heart;
import com.example.crudw.demo.Heart.HeartService;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.comment.Comment;
import com.example.crudw.demo.comment.CommentRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final HeartService heartService;

    public BoardController(BoardService boardService, CommentService commentService, HeartService heartService){
        this.boardService = boardService;
        this.commentService = commentService;
        this.heartService = heartService;
    }

    @GetMapping(value = "/write")
    public String write() {
        return "write";
    }

    @PostMapping("/write")
    public String writePost(@RequestParam("title") String title,
                            @RequestParam("writerName") String writerName,
                            @RequestParam("writerNo")String writerNo,
                            @RequestParam("content") String content,
                            @RequestParam(value="file", required = false) MultipartFile file){
        Board board = new Board();
        board.setTitle(title);
        board.setWriterName(writerName);
        board.setWriterNo(Long.valueOf(writerNo));
        board.setContent(content);
        board.setFileName(file.getOriginalFilename());
        board.setFileLink(file.getOriginalFilename());

        boardService.savePost(board); //db에 저장...
        return "redirect:/list";
    }

    @GetMapping(value = "/deletePost/{boardNo}")
    public String deletePost(@PathVariable("boardNo")Long boardNo, HttpSession session){
        Object no = session.getAttribute("no");
        if (no == null) {
            System.out.println("⚠ 세션이 만료되었거나 존재하지 않습니다.");
            return "redirect:/login";
        }
        Board board = boardService.getPost(boardNo);
        if (board != null && no.equals(board.getWriterNo())) {
            commentService.deleteComment(boardNo);
            boardService.deletePost(boardNo);
            System.out.println("✅ 게시글 삭제 성공!");

        } else {
            System.out.println("❌ 게시글 삭제 조건 불충족! (작성자 불일치)");
        }

        return "redirect:/list";
    }
    @GetMapping("/update/post/{boardNo}")
    public String updatePost(@PathVariable Long boardNo, Model model){
        Board board = boardService.getPost(boardNo);
        model.addAttribute("board",board);
        return "update";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute Board board) {
        boardService.updateBoard(board.getNo(), board);
        return "redirect:/read/" + board.getNo();
    }

    @GetMapping(value = {"/list", "/search"})
    public String listAndSearch(@RequestParam(value = "searchStr", required = false) String searchStr,
                                @RequestParam(value = "searchType",required = false) String searchType,
                                Model model,
                                @PageableDefault(size = 10, sort = "no", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> boardPage;

        if (searchStr != null && !searchStr.isEmpty()) { //searchStr가 비어있지않다면 검색기능 수행
            List<Board> searchList = boardService.search(searchStr,searchType);
            boardPage = new PageImpl<>(searchList, pageable, searchList.size());
            model.addAttribute("searchList", searchList);
        } else {//searchStr가 비어있다면 그냥 페이징 처리된거 보여줘야징
            boardPage = boardService.pageList(pageable);
        }

        int startPage = Math.max(1, boardPage.getPageable().getPageNumber() - 10); //10개의 게시글 보여줘야지
        int endPage = Math.min(boardPage.getTotalPages(), boardPage.getPageable().getPageNumber() + 10);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNext", boardPage.hasNext()); //다음페이지
        model.addAttribute("hasPrev", boardPage.hasPrevious()); //이전페이지
        model.addAttribute("nboard", boardPage);
        return "list"; // 검색 + 페이징 처리 통합
    }

    @GetMapping(value = "/read/{no}")
    public String read(@PathVariable("no") Long no, Model model, @ModelAttribute Comment comment, HttpServletRequest request, Heart heart) {
        List<CommentRequestDto> commentList = commentService.getComments(no);
        HttpSession session = request.getSession();

        Object userIdObj = session.getAttribute("no");
        Long userId = null;

        if (userIdObj != null) {
            userId = (Long) userIdObj;
        } else {
            // 사용자가 로그인하지 않은 경우, 0과 같은 비사용자 ID를 할당
            userId = 0L;
        }

        model.addAttribute("like",heartService.findLike(no, Long.valueOf(userId))); //게시글 읽을때 좋아요 눌렀는지 확인?
        model.addAttribute("board", boardService.getPost(no));
        model.addAttribute("comment",commentList); //댓글을 보여주기
        model.addAttribute("comments",comment);
        model.addAttribute("heart",heart);
        model.addAttribute("board_no",no);
        return "detailboard";
    }

}
