package com.campus.campuscommunity.domain.comment.repository;

import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.comment.entity.Comment;
import com.campus.campuscommunity.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 삭제되지 않은 댓글 단일 조회
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Comment> findActiveById(@Param("id") Long id);

    // 특정 게시글의 모든 일반 댓글 조회 (대댓글 제외, 삭제되지 않은 것만)
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId AND c.parent IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findActiveToplevelByBoardId(@Param("boardId") Long boardId);

    // 특정 게시글의 모든 댓글 조회 (대댓글 포함, 삭제되지 않은 것만)
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findActiveByBoardId(@Param("boardId") Long boardId);

    // 특정 게시글의 모든 댓글 페이징 조회
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    Page<Comment> findActiveByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    // 특정 부모 댓글의 대댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<Comment> findActiveRepliesByParentId(@Param("parentId") Long parentId);

    // 특정 사용자가 작성한 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.writer.id = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Comment> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    // 특정 게시글의 댓글 수 카운트
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.id = :boardId AND c.isDeleted = false")
    Long countActiveByBoardId(@Param("boardId") Long boardId);

    // 특정 게시글의 일반 댓글 수 카운트 (대댓글 제외)
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.id = :boardId AND c.parent IS NULL AND c.isDeleted = false")
    Long countActiveToplevelByBoardId(@Param("boardId") Long boardId);

    // 특정 부모 댓글의 대댓글 수 카운트
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent.id = :parentId AND c.isDeleted = false")
    Long countActiveRepliesByParentId(@Param("parentId") Long parentId);
}