package com.campus.campuscommunity.domain.board.entity;

import com.campus.campuscommunity.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"board_id", "user_id"})
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 좋아요 상태 변경 메서드
    public void changeStatus(LikeStatus status) {
        this.status = status;
    }

    // 생성 시간을 설정하는 메서드 (JPA Auditing이 작동하지 않는 경우를 대비)
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // 좋아요 상태 열거형
    public enum LikeStatus {
        LIKE,    // 좋아요
        DISLIKE  // 싫어요
    }
}