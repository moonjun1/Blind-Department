package com.campus.campuscommunity.domain.comment.entity;

import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // 자동으로 생성/수정 시간 관리
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private String writerDepartment; // 작성자 학과 (익명 표시용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글 (대댓글인 경우)

    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0; // 좋아요 수

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 일시

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부 (soft delete)

    // 좋아요 수 증가 메서드
    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    // 좋아요 수 감소 메서드
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    // 댓글 내용 수정 메서드
    public void update(String content) {
        this.content = content;
    }

    // 댓글 삭제 메서드 (soft delete)
    public void delete() {
        this.isDeleted = true;
    }
}