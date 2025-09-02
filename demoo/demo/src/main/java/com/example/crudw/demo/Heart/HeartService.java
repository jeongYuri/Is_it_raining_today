package com.example.crudw.demo.Heart;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;

    //좋아요 추가
    @Transactional
    public void addHeart(Long userId, Long boardId){
        System.out.println("나는 board야"+boardId);
        System.out.println("나는 user야"+userId);
        if(heartRepository.findHeartByUserIdAndBoardId(userId, boardId).isPresent()){
            throw new IllegalStateException("이미 좋아요를 누른 상태입니다.🥹");
        }
        Heart heart = new Heart(userId, boardId);
        System.out.println("저장할 Heart: " + heart);
        heartRepository.save(heart);
    }

    //좋아요 취소
    @Transactional
    public void removeHeart(Long userId, Long boardId){
        Optional<Heart> heart = heartRepository.findHeartByUserIdAndBoardId(userId, boardId);
        if(heart.isEmpty()){
            throw new IllegalStateException("좋아요가 존재하지 않습니다.🥹");
        }
        heartRepository.deleteHeart(userId, boardId);
    }

    public boolean findLike(Long userId, Long boardId) {
        return heartRepository.findHeartByUserIdAndBoardId(userId, boardId).isPresent();
    }

}
