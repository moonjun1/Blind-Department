package com.campus.campuscommunity.domain.board.repository;

import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.board.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 삭제되지 않은 게시글 단일 조회
    @Query("SELECT b FROM Board b WHERE b.id = :id AND b.isDeleted = false")
    Optional<Board> findActiveById(@Param("id") Long id);

    // 페이징으로 삭제되지 않은 모든 게시글 조회
    Page<Board> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 카테고리별 게시글 조회
    Page<Board> findByIsDeletedFalseAndCategoryOrderByCreatedAtDesc(BoardCategory category, Pageable pageable);

    // 학과별 게시글 조회
    Page<Board> findByIsDeletedFalseAndWriterDepartmentOrderByCreatedAtDesc(String department, Pageable pageable);

    // 특정 사용자의 게시글 조회
    Page<Board> findByIsDeletedFalseAndWriter_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 인기 게시글 조회 (좋아요 수 기준)
    Page<Board> findByIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    // 제목 또는 내용으로 게시글 검색
    @Query("SELECT b FROM Board b WHERE b.isDeleted = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) ORDER BY b.createdAt DESC")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}