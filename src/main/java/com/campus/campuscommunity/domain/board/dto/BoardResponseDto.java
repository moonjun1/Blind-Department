package com.campus.campuscommunity.domain.board.dto;

import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.board.entity.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 관련 응답 DTO 클래스들을 정의합니다.
 */
@Schema(description = "게시판 응답 DTO")
public class BoardResponseDto {

    /**
     * 게시글 상세 정보 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 상세 정보 응답 DTO")
    public static class BoardDetailResponse {

        @Schema(description = "게시글 ID", example = "1")
        private Long id;

        @Schema(description = "게시글 제목", example = "안녕하세요, 첫 게시글입니다.")
        private String title;

        @Schema(description = "게시글 내용", example = "블라인드 커뮤니티 기능 테스트 중입니다. 학과별로 익명성이 보장되는지 확인해봅시다.")
        private String content;

        @Schema(description = "작성자 학과 (익명 표시용)", example = "컴퓨터공학과")
        private String writerDepartment;

        @Schema(description = "작성자 ID (본인 확인용, 외부에 노출되지 않음)", example = "1")
        private Long writerId;

        @Schema(description = "카테고리", example = "FREE")
        private BoardCategory category;

        @Schema(description = "카테고리 표시 이름", example = "자유게시판")
        private String categoryDisplayName;

        @Schema(description = "조회수", example = "42")
        private Integer viewCount;

        @Schema(description = "좋아요 수", example = "15")
        private Integer likeCount;

        @Schema(description = "싫어요 수", example = "3")
        private Integer dislikeCount;

        @Schema(description = "생성 시간", example = "2025-04-15T14:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "수정 시간", example = "2025-04-15T15:45:00")
        private LocalDateTime updatedAt;

        @Schema(description = "현재 사용자의 좋아요 상태 (NONE, LIKE, DISLIKE)", example = "LIKE")
        private String likeStatus;

        // Board 엔티티에서 DTO 생성 (기본 변환)
        public static BoardDetailResponse from(Board board) {
            return from(board, "NONE");
        }

        // Board 엔티티에서 DTO 생성 (좋아요 상태 포함)
        public static BoardDetailResponse from(Board board, String likeStatus) {
            return BoardDetailResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .writerDepartment(board.getWriterDepartment())
                    .writerId(board.getWriter().getId())
                    .category(board.getCategory())
                    .categoryDisplayName(board.getCategory().getDisplayName())
                    .viewCount(board.getViewCount())
                    .likeCount(board.getLikeCount())
                    .dislikeCount(board.getDislikeCount())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .likeStatus(likeStatus)
                    .build();
        }
    }

    /**
     * 게시글 목록 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 목록 응답 DTO")
    public static class BoardListResponse {

        @Schema(description = "게시글 목록")
        private List<BoardSummary> boards;

        @Schema(description = "전체 페이지 수", example = "5")
        private int totalPages;

        @Schema(description = "전체 게시글 수", example = "42")
        private long totalElements;

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int currentPage;
    }

    /**
     * 게시글 요약 정보 (목록용)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 요약 정보 (목록용)")
    public static class BoardSummary {

        @Schema(description = "게시글 ID", example = "1")
        private Long id;

        @Schema(description = "게시글 제목", example = "안녕하세요, 첫 게시글입니다.")
        private String title;

        @Schema(description = "작성자 학과 (익명 표시용)", example = "컴퓨터공학과")
        private String writerDepartment;

        @Schema(description = "카테고리", example = "FREE")
        private BoardCategory category;

        @Schema(description = "카테고리 표시 이름", example = "자유게시판")
        private String categoryDisplayName;

        @Schema(description = "조회수", example = "42")
        private Integer viewCount;

        @Schema(description = "좋아요 수", example = "15")
        private Integer likeCount;

        @Schema(description = "댓글 수", example = "7")
        private Integer commentCount;

        @Schema(description = "생성 시간", example = "2025-04-15T14:30:00")
        private LocalDateTime createdAt;

        // Board 엔티티에서 요약 정보 생성
        public static BoardSummary from(Board board) {
            return BoardSummary.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .writerDepartment(board.getWriterDepartment())
                    .category(board.getCategory())
                    .categoryDisplayName(board.getCategory().getDisplayName())
                    .viewCount(board.getViewCount())
                    .likeCount(board.getLikeCount())
                    .commentCount(0) // 댓글 기능 구현 시 업데이트
                    .createdAt(board.getCreatedAt())
                    .build();
        }

        // Board 엔티티 리스트에서 BoardSummary 리스트 생성
        public static List<BoardSummary> fromList(List<Board> boards) {
            return boards.stream()
                    .map(BoardSummary::from)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 게시글 좋아요/싫어요 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 좋아요/싫어요 응답 DTO")
    public static class LikeResponse {

        @Schema(description = "게시글 ID", example = "1")
        private Long boardId;

        @Schema(description = "변경된 좋아요 상태 (NONE, LIKE, DISLIKE)", example = "LIKE")
        private String likeStatus;

        @Schema(description = "현재 좋아요 수", example = "16")
        private Integer likeCount;

        @Schema(description = "현재 싫어요 수", example = "3")
        private Integer dislikeCount;
    }
}