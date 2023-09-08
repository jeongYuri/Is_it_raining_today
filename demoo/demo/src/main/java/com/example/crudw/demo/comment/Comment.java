package com.example.crudw.demo.comment;

import com.example.crudw.demo.Repository.MemoryUserRepository;
import com.example.crudw.demo.TimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment extends TimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long no;
    @Column(name = "board_no")
    private Long boardNo;

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private boolean isDeleted;//삭제시 삭제되었습니당 하려공..

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_no")
    private Comment parentNo;

    @Builder.Default
    @OneToMany(mappedBy = "parentNo", orphanRemoval = true)  //부모 댓글 삭제시 관련 자식댓글들 다 삭제..~~가 안되야만 함...
    private List<Comment> children = new ArrayList<>();

    @Column(name = "writer_no")
    private Long writerNo;

    @Column(name = "writer_name")
    private String writerName;

    private String content;


    public void changeIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        this.writerName = isDeleted ? "(알수없음)" : null;
        this.content = isDeleted ? "삭제되었습니다." : null;
    }
    public void updateParent(Comment comment){
        this.parentNo = comment;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }
}
