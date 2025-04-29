package com.campus.campuscommunity.domain.board.dto;

import com.campus.campuscommunity.domain.board.entity.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 관련 요청 DTO 클래스들을 정의합니다.
 */
@Schema(description = "게시판 요청 DTO")
public class BoardRequestDto {

    /**
     * 게시글 생성 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 작성 요청 DTO")
    public static class CreateRequest {

        @Schema(description = "게시글 제목", example = "안녕하세요, 첫 게시글입니다.", required = true)
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
        private String title;

        @Schema(description = "게시글 내용", example = "블라인드 커뮤니티 기능 테스트 중입니다. 학과별로 익명성이 보장되는지 확인해봅시다.", required = true)
        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Schema(description = "게시글 카테고리", example = "FREE", required = true,
                allowableValues = {"COMPUTER_SCIENCE", "ELECTRONICS", "MECHANICAL", "MANAGEMENT",
                        "CAREER", "STUDY", "CLUB", "CAMPUS_LIFE", "FREE", "QNA"})
        @NotNull(message = "카테고리는 필수 입력값입니다.")
        private BoardCategory category;
    }

    /**
     * 게시글 수정 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 수정 요청 DTO")
    public static class UpdateRequest {

        @Schema(description = "게시글 제목", example = "수정된 제목입니다.", required = true)
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
        private String title;

        @Schema(description = "게시글 내용", example = "게시글 내용을 수정했습니다. 더 자세한 내용을 추가했습니다.", required = true)
        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Schema(description = "게시글 카테고리", example = "QNA", required = true,
                allowableValues = {"COMPUTER_SCIENCE", "ELECTRONICS", "MECHANICAL", "MANAGEMENT",
                        "CAREER", "STUDY", "CLUB", "CAMPUS_LIFE", "FREE", "QNA"})
        @NotNull(message = "카테고리는 필수 입력값입니다.")
        private BoardCategory category;
    }

    /**
     * 게시글 검색 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 검색 요청 DTO")
    public static class SearchRequest {

        @Schema(description = "검색 키워드 (제목 또는 내용에 포함된 키워드)", example = "스터디")
        private String keyword;

        @Schema(description = "카테고리 필터", example = "STUDY",
                allowableValues = {"COMPUTER_SCIENCE", "ELECTRONICS", "MECHANICAL", "MANAGEMENT",
                        "CAREER", "STUDY", "CLUB", "CAMPUS_LIFE", "FREE", "QNA"})
        private BoardCategory category;

        @Schema(description = "학과 필터", example = "컴퓨터공학과")
        private String department;

        @Schema(description = "정렬 기준", example = "created", defaultValue = "created",
                allowableValues = {"created", "views", "likes"})
        @Builder.Default
        private String sort = "created";

        @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
        @Builder.Default
        private int page = 0;

        @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
        @Builder.Default
        private int size = 10;
    }

    /**
     * 게시글 좋아요/싫어요 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "게시글 좋아요/싫어요 요청 DTO")
    public static class LikeRequest {

        @Schema(description = "좋아요 여부 (true: 좋아요, false: 싫어요)", example = "true", required = true)
        @NotNull(message = "좋아요 여부는 필수 입력값입니다.")
        private Boolean isLike;
    }
}