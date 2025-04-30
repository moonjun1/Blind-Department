package com.campus.campuscommunity.domain.comment.dto;

import com.campus.campuscommunity.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 댓글 관련 응답 DTO 클래스들을 정의합니다.
 */
@Schema(description = "댓글 응답 DTO")
public class CommentResponseDto {

    /**
     * 댓글 정보 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 정보 응답 DTO")
    public static class CommentInfo {

        @Schema(description = "댓글 ID", example = "1")
        private Long id;

        @Schema(description = "댓글 내용", example = "안녕하세요, 좋은 정보 감사합니다!")
        private String content;

        @Schema(description = "작성자 ID (본인 확인용, 외부에 노출되지 않음)", example = "1")
        private Long writerId;

        @Schema(description = "작성자 학과 (익명 표시용)", example = "컴퓨터공학과")
        private String writerDepartment;

        @Schema(description = "게시글 ID", example = "1")
        private Long boardId;

        @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "3")
        private Long parentId;

        @Schema(description = "좋아요 수", example = "5")
        private Integer likeCount;

        @Schema(description = "생성 시간", example = "2025-04-30T01:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "수정 시간", example = "2025-04-30T01:45:00")
        private LocalDateTime updatedAt;

        @Schema(description = "대댓글 목록")
        private List<CommentInfo> replies;

        @Schema(description = "대댓글 수", example = "3")
        private Integer replyCount;

        @Schema(description = "좋아요 여부 (현재 사용자가 좋아요 했는지)", example = "true")
        private Boolean liked;

        // Comment 엔티티에서 DTO 생성 (기본 변환)
        public static CommentInfo from(Comment comment) {
            return from(comment, false, 0);
        }

        // Comment 엔티티에서 DTO 생성 (좋아요 여부 포함)
        public static CommentInfo from(Comment comment, boolean liked, int replyCount) {
            return CommentInfo.builder()
                    .id(comment.getId())
                    .content(comment.isDeleted() ? "(삭제된 댓글입니다)" : comment.getContent())
                    .writerId(comment.getWriter().getId())
                    .writerDepartment(comment.getWriterDepartment())
                    .boardId(comment.getBoard().getId())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .likeCount(comment.getLikeCount())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .replies(new ArrayList<>()) // 초기에는 빈 리스트로 설정
                    .replyCount(replyCount)
                    .liked(liked)
                    .build();
        }

        // 대댓글 설정 메서드
        public void setReplies(List<CommentInfo> replies) {
            this.replies = replies;
        }
    }

    /**
     * 댓글 목록 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 목록 응답 DTO")
    public static class CommentListResponse {

        @Schema(description = "댓글 목록")
        private List<CommentInfo> comments;

        @Schema(description = "전체 댓글 수", example = "15")
        private Long totalComments;

        // 댓글 목록 변환 - 계층 구조로 구성 (대댓글을 부모 댓글 아래에 포함)
        public static CommentListResponse from(List<Comment> topLevelComments, List<Comment> allReplies,
                                               Map<Long, Boolean> likeStatusMap, Map<Long, Integer> replyCountMap) {
            // 대댓글 매핑 (부모 ID -> 대댓글 목록)
            Map<Long, List<Comment>> repliesByParentId = allReplies.stream()
                    .collect(Collectors.groupingBy(reply -> reply.getParent().getId()));

            // 일반 댓글 변환
            List<CommentInfo> commentInfoList = topLevelComments.stream()
                    .map(comment -> {
                        Long commentId = comment.getId();
                        boolean liked = likeStatusMap.getOrDefault(commentId, false);
                        int replyCount = replyCountMap.getOrDefault(commentId, 0);

                        CommentInfo commentInfo = CommentInfo.from(comment, liked, replyCount);

                        // 해당 댓글의 대댓글이 있으면 처리
                        if (repliesByParentId.containsKey(commentId)) {
                            List<CommentInfo> replyInfos = repliesByParentId.get(commentId).stream()
                                    .map(reply -> {
                                        Long replyId = reply.getId();
                                        boolean replyLiked = likeStatusMap.getOrDefault(replyId, false);
                                        return CommentInfo.from(reply, replyLiked, 0);
                                    })
                                    .collect(Collectors.toList());

                            commentInfo.setReplies(replyInfos);
                        }

                        return commentInfo;
                    })
                    .collect(Collectors.toList());

            return CommentListResponse.builder()
                    .comments(commentInfoList)
                    .totalComments((long) (topLevelComments.size() + allReplies.size()))
                    .build();
        }
    }

    /**
     * 댓글 작성/수정 결과 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 작성/수정 결과 응답 DTO")
    public static class CommentActionResponse {

        @Schema(description = "댓글 정보")
        private CommentInfo comment;

        @Schema(description = "게시글 ID", example = "1")
        private Long boardId;

        @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "3")
        private Long parentId;

        // Comment 엔티티에서 DTO 생성
        public static CommentActionResponse from(Comment comment, boolean liked) {
            return CommentActionResponse.builder()
                    .comment(CommentInfo.from(comment, liked, 0))
                    .boardId(comment.getBoard().getId())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .build();
        }
    }
}