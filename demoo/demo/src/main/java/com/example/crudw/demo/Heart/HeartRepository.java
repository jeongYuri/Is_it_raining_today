package com.example.crudw.demo.Heart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Heart h WHERE h.userId = :userId AND h.boardId = :boardId")
    void deleteHeart(@Param("userId")Long userId, @Param("boardId")Long boardId); //좋아요 취소

    @Query("SELECT h FROM Heart h WHERE h.userId = :userId AND h.boardId =:boardId")
    Optional<Heart> findHeartByUserIdAndBoardId(@Param("userId")Long userId, @Param("boardId")Long boardId); //특정 유저가 특정 게시글에 좋아요 눌렀는지 확인

    //특정 게시글의 좋아요 리스트 조회
    List<Heart> findByBoardId(Long no);
}
