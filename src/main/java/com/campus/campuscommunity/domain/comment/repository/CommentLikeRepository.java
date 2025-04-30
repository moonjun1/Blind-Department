package com.campus.campuscommunity.domain.comment.repository;

import com.campus.campuscommunity.domain.comment.entity.Comment;
import com.campus.campuscommunity.domain.comment.entity.CommentLike;
import com.campus.campuscommunity.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 특정 사용자와 댓글의 좋아요 기록 찾기
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    // 특정 댓글의 좋아요 수 계산
    long countByComment(Comment comment);

    // 특정 사용자가 특정 댓글에 좋아요를 했는지 확인
    boolean existsByCommentAndUser(Comment comment, User user);

    // 특정 댓글의 모든 좋아요 삭제 (댓글 삭제 시 사용)
    void deleteAllByComment(Comment comment);
}