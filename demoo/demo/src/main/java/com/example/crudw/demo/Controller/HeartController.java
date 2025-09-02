package com.example.crudw.demo.Controller;

import com.example.crudw.demo.Heart.HeartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class HeartController {
    private final HeartService heartService;

    public HeartController(HeartService heartService){
        this.heartService = heartService;
    }

    @PostMapping("/{no}/like")
    public ResponseEntity<String> addLike(@PathVariable Long no,@RequestParam Long id){
        try{
            System.out.println("나는 board🎃🎃"+no);
            System.out.println("나는 user📌📌"+id);
            heartService.addHeart(id, no);
            System.out.println("좋아요 추가 완료");
            return ResponseEntity.ok("좋아요 추가 완료");
        }catch (IllegalStateException e){
            System.out.println("에러 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 좋아요를 누른 상태입니다.");
        }

    }
    //좋아요 취소
    @PostMapping("/{no}/unlike")
    @ResponseBody
    public ResponseEntity<String> removeLike(@PathVariable Long no, @RequestParam Long id){
        try{
            heartService.removeHeart(id,no);
            return ResponseEntity.ok("좋아요 취소 완료");
        }catch(IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요가 존재하지 않습니다.");
        }
    }
}
