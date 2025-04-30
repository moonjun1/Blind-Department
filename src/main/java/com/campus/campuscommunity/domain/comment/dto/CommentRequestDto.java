package com.campus.campuscommunity.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 관련 요청 DTO 클래스들을 정의합니다.
 */
@Schema(description = "댓글 요청 DTO")
public class CommentRequestDto {

    /**
     * 댓글 작성 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 작성 요청 DTO")
    public static class CreateRequest {

        @Schema(description = "댓글 내용", example = "안녕하세요, 좋은 정보 감사합니다!", required = true)
        @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
        @Size(min = 2, max = 1000, message = "댓글은 2자 이상 1000자 이하로 입력해주세요.")
        private String content;

        @Schema(description = "게시글 ID", example = "1", required = true)
        @NotNull(message = "게시글 ID는 필수 입력값입니다.")
        private Long boardId;

        @Schema(description = "부모 댓글 ID (대댓글인 경우에만 입력)", example = "3")
        private Long parentId;
    }

    /**
     * 댓글 수정 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 수정 요청 DTO")
    public static class UpdateRequest {

        @Schema(description = "댓글 내용", example = "내용을 수정합니다. 더 자세한 의견을 작성했습니다.", required = true)
        @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
        @Size(min = 2, max = 1000, message = "댓글은 2자 이상 1000자 이하로 입력해주세요.")
        private String content;
    }
}