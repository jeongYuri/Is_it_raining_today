package com.example.crudw.demo.Heart;

import lombok.Getter;
import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;

@Getter
@Entity
@Table(name="likes")
public class Heart extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "board_id")
    private Long boardId;

    public Heart(Long userId, Long boardId) {
        this.userId = userId;
        this.boardId = boardId;
    }

    public Heart() {

    }
}
