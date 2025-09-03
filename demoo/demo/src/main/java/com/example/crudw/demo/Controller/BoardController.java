package com.example.crudw.demo.Controller;


import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Heart.Heart;
import com.example.crudw.demo.Heart.HeartService;
import com.example.crudw.demo.Member.User;
import com.example.crudw.demo.Service.BoardService;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.Service.UserService;
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
    private final UserService userService;

    public BoardController(BoardService boardService, CommentService commentService, HeartService heartService, UserService userService){
        this.boardService = boardService;
        this.commentService = commentService;
        this.heartService = heartService;
        this.userService = userService;
    }

    @GetMapping(value = "/write")
    public String write() {
        return "write";
    }

    @PostMapping("/write")
    public String writePost(@RequestParam("title") String title,
                            @RequestParam("content") String content,
                            @RequestParam(value="file", required = false) MultipartFile file,
                            HttpServletRequest request){
        HttpSession session = request.getSession();
        String writerId = (String) session.getAttribute("id");
        if (writerId == null) {
            writerId = (String) session.getAttribute("name");
        }

        User writer = userService.getUser(writerId);

        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);

        // 세션에서 가져온 사용자 정보로 writerId과 writerNo 설정
        if (writer != null) {
            board.setWriterName(writer.getId());
            board.setWriterNo(writer.getNo());
        }

        // 파일 처리 (이전 로직은 유지)
        if (file != null && !file.isEmpty()) {
            board.setFileName(file.getOriginalFilename());
            board.setFileLink(file.getOriginalFilename());
        }

        boardService.savePost(board);
        return "redirect:/list";
    }

    @GetMapping(value = "/deletePost/{boardNo}")
    public String deletePost(@PathVariable("boardNo")Long boardNo, HttpSession session){
        Long userNo = (Long) session.getAttribute("userNo");
        if (userNo == null) {
            System.out.println("⚠ 세션이 만료되었거나 존재하지 않습니다.");
            return "redirect:/login";
        }
        Board board = boardService.getPost(boardNo);
        if (board != null && board.getWriterNo() != null && userNo.equals(board.getWriterNo())) {
            try {
                // 게시글 삭제 전 댓글 삭제 시도 (오류가 나도 게시글 삭제는 진행)
                commentService.deleteComment(boardNo);
                System.out.println("✅ 댓글 삭제 성공 (게시글 " + boardNo + ")");
            } catch (Exception e) {
                System.out.println("❌ 댓글 삭제 실패: " + e.getMessage());
            }
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
