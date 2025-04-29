package com.campus.campuscommunity.domain.board.entity;

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
@Table(name = "boards")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // 자동으로 생성/수정 시간 관리
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Column(nullable = false)
    private String writerDepartment; // 작성자 학과 (익명 표시용)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory category;

    @Builder.Default
    @Column(nullable = false)
    private Integer viewCount = 0; // 조회수

    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0; // 좋아요 수

    @Builder.Default
    @Column(nullable = false)
    private Integer dislikeCount = 0; // 싫어요 수

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 일시

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부 (soft delete)

    // 조회수 증가 메서드
    public void increaseViewCount() {
        this.viewCount += 1;
    }

    // 좋아요 수 증가 메서드
    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    // 좋아요 수 감소 메서드
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    // 싫어요 수 증가 메서드
    public void increaseDislikeCount() {
        this.dislikeCount += 1;
    }

    // 싫어요 수 감소 메서드
    public void decreaseDislikeCount() {
        this.dislikeCount = Math.max(0, this.dislikeCount - 1);
    }

    // 게시글 내용 수정 메서드
    public void update(String title, String content, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // 게시글 삭제 메서드 (soft delete)
    public void delete() {
        this.isDeleted = true;
    }
}