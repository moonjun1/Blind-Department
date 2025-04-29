package com.campus.campuscommunity.domain.board.repository;

import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.board.entity.BoardLike;
import com.campus.campuscommunity.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    // 특정 사용자와 게시글의 좋아요/싫어요 기록 찾기
    Optional<BoardLike> findByBoardAndUser(Board board, User user);

    // 특정 게시글의 좋아요 수 계산
    long countByBoardAndStatus(Board board, BoardLike.LikeStatus status);

    // 특정 사용자가 특정 게시글에 좋아요/싫어요를 했는지 확인
    boolean existsByBoardAndUserAndStatus(Board board, User user, BoardLike.LikeStatus status);

    // 특정 게시글의 모든 좋아요/싫어요 삭제 (게시글 삭제 시 사용)
    void deleteAllByBoard(Board board);
}