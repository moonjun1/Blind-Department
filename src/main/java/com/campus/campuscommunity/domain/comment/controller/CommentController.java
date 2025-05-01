package com.campus.campuscommunity.domain.comment.controller;

import com.campus.campuscommunity.global.common.response.ApiResponse;
import com.campus.campuscommunity.domain.comment.dto.CommentRequestDto;
import com.campus.campuscommunity.domain.comment.dto.CommentResponseDto;
import com.campus.campuscommunity.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글 관리", description = "댓글 작성, 조회, 수정, 삭제 및 좋아요 API")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성 API
     * POST /api/comments
     */
    @Operation(
            summary = "댓글 작성",
            description = "새로운 댓글을 작성합니다. 댓글 내용, 게시글 ID가 필요하며, 대댓글인 경우 부모 댓글 ID도 필요합니다. 작성자는 익명으로 학과만 표시됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "댓글 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDto.CommentActionResponse.class),
                            examples = @ExampleObject(
                                    name = "댓글 작성 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 201,\n" +
                                            "  \"message\": \"댓글이 성공적으로 작성되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"comment\": {\n" +
                                            "      \"id\": 1,\n" +
                                            "      \"content\": \"안녕하세요, 좋은 정보 감사합니다!\",\n" +
                                            "      \"writerId\": 1,\n" +
                                            "      \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "      \"boardId\": 1,\n" +
                                            "      \"parentId\": null,\n" +
                                            "      \"likeCount\": 0,\n" +
                                            "      \"createdAt\": \"2025-04-30T01:30:00\",\n" +
                                            "      \"updatedAt\": \"2025-04-30T01:30:00\",\n" +
                                            "      \"replies\": [],\n" +
                                            "      \"replyCount\": 0,\n" +
                                            "      \"liked\": false\n" +
                                            "    },\n" +
                                            "    \"boardId\": 1,\n" +
                                            "    \"parentId\": null\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 또는 부모 댓글을 찾을 수 없음"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto.CommentActionResponse>> createComment(
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email,
            @Parameter(description = "댓글 작성 정보", required = true)
            @Valid @RequestBody CommentRequestDto.CreateRequest request) {

        CommentResponseDto.CommentActionResponse response = commentService.createComment(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 성공적으로 작성되었습니다.", response));
    }

    /**
     * 댓글 수정 API
     * PUT /api/comments/{id}
     */
    @Operation(
            summary = "댓글 수정",
            description = "댓글을 수정합니다. 댓글 내용을 수정할 수 있습니다. 작성자만 수정 가능합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDto.CommentActionResponse.class),
                            examples = @ExampleObject(
                                    name = "댓글 수정 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"댓글이 성공적으로 수정되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"comment\": {\n" +
                                            "      \"id\": 1,\n" +
                                            "      \"content\": \"내용을 수정합니다. 더 자세한 의견을 작성했습니다.\",\n" +
                                            "      \"writerId\": 1,\n" +
                                            "      \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "      \"boardId\": 1,\n" +
                                            "      \"parentId\": null,\n" +
                                            "      \"likeCount\": 5,\n" +
                                            "      \"createdAt\": \"2025-04-30T01:30:00\",\n" +
                                            "      \"updatedAt\": \"2025-04-30T01:45:00\",\n" +
                                            "      \"replies\": [],\n" +
                                            "      \"replyCount\": 0,\n" +
                                            "      \"liked\": true\n" +
                                            "    },\n" +
                                            "    \"boardId\": 1,\n" +
                                            "    \"parentId\": null\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (작성자가 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto.CommentActionResponse>> updateComment(
            @Parameter(description = "댓글 ID", example = "1", required = true)
            @PathVariable("id") Long commentId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email,
            @Parameter(description = "댓글 수정 정보", required = true)
            @Valid @RequestBody CommentRequestDto.UpdateRequest request) {

        CommentResponseDto.CommentActionResponse response = commentService.updateComment(commentId, email, request);
        return ResponseEntity.ok(ApiResponse.success("댓글이 성공적으로 수정되었습니다.", response));
    }

    /**
     * 댓글 삭제 API
     * DELETE /api/comments/{id}
     */
    @Operation(
            summary = "댓글 삭제",
            description = "댓글을 삭제합니다. (소프트 딜리트) 작성자만 삭제 가능합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "댓글 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (작성자가 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "댓글 ID", example = "1", required = true)
            @PathVariable("id") Long commentId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email) {

        commentService.deleteComment(commentId, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 좋아요 API
     * POST /api/comments/{id}/like
     */
    @Operation(
            summary = "댓글 좋아요",
            description = "댓글에 좋아요를 추가하거나 취소합니다. 이미 좋아요한 상태에서 다시 요청하면 좋아요가 취소됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "좋아요 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDto.CommentActionResponse.class),
                            examples = @ExampleObject(
                                    name = "좋아요 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"좋아요가 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"comment\": {\n" +
                                            "      \"id\": 1,\n" +
                                            "      \"content\": \"안녕하세요, 좋은 정보 감사합니다!\",\n" +
                                            "      \"writerId\": 1,\n" +
                                            "      \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "      \"boardId\": 1,\n" +
                                            "      \"parentId\": null,\n" +
                                            "      \"likeCount\": 6,\n" +
                                            "      \"createdAt\": \"2025-04-30T01:30:00\",\n" +
                                            "      \"updatedAt\": \"2025-04-30T01:30:00\",\n" +
                                            "      \"replies\": [],\n" +
                                            "      \"replyCount\": 0,\n" +
                                            "      \"liked\": true\n" +
                                            "    },\n" +
                                            "    \"boardId\": 1,\n" +
                                            "    \"parentId\": null\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<CommentResponseDto.CommentActionResponse>> likeComment(
            @Parameter(description = "댓글 ID", example = "1", required = true)
            @PathVariable("id") Long commentId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email) {

        CommentResponseDto.CommentActionResponse response = commentService.toggleLike(commentId, email);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 처리되었습니다.", response));
    }

    /**
     * 게시글의 댓글 목록 조회 API
     * GET /api/comments/board/{boardId}
     */
    @Operation(
            summary = "게시글의 댓글 목록 조회",
            description = "특정 게시글의 댓글 목록을 조회합니다. 대댓글은 부모 댓글 아래에 중첩되어 표시됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDto.CommentListResponse.class),
                            examples = @ExampleObject(
                                    name = "댓글 목록 조회 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"comments\": [\n" +
                                            "      {\n" +
                                            "        \"id\": 1,\n" +
                                            "        \"content\": \"안녕하세요, 좋은 정보 감사합니다!\",\n" +
                                            "        \"writerId\": 1,\n" +
                                            "        \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "        \"boardId\": 1,\n" +
                                            "        \"parentId\": null,\n" +
                                            "        \"likeCount\": 5,\n" +
                                            "        \"createdAt\": \"2025-04-30T01:30:00\",\n" +
                                            "        \"updatedAt\": \"2025-04-30T01:30:00\",\n" +
                                            "        \"replies\": [\n" +
                                            "          {\n" +
                                            "            \"id\": 3,\n" +
                                            "            \"content\": \"저도 동의합니다!\",\n" +
                                            "            \"writerId\": 2,\n" +
                                            "            \"writerDepartment\": \"전자공학과\",\n" +
                                            "            \"boardId\": 1,\n" +
                                            "            \"parentId\": 1,\n" +
                                            "            \"likeCount\": 2,\n" +
                                            "            \"createdAt\": \"2025-04-30T01:35:00\",\n" +
                                            "            \"updatedAt\": \"2025-04-30T01:35:00\",\n" +
                                            "            \"replies\": [],\n" +
                                            "            \"replyCount\": 0,\n" +
                                            "            \"liked\": false\n" +
                                            "          }\n" +
                                            "        ],\n" +
                                            "        \"replyCount\": 1,\n" +
                                            "        \"liked\": true\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"id\": 2,\n" +
                                            "        \"content\": \"질문이 있습니다.\",\n" +
                                            "        \"writerId\": 3,\n" +
                                            "        \"writerDepartment\": \"경영학과\",\n" +
                                            "        \"boardId\": 1,\n" +
                                            "        \"parentId\": null,\n" +
                                            "        \"likeCount\": 1,\n" +
                                            "        \"createdAt\": \"2025-04-30T01:32:00\",\n" +
                                            "        \"updatedAt\": \"2025-04-30T01:32:00\",\n" +
                                            "        \"replies\": [],\n" +
                                            "        \"replyCount\": 0,\n" +
                                            "        \"liked\": false\n" +
                                            "      }\n" +
                                            "    ],\n" +
                                            "    \"totalComments\": 3\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            )
    })
    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<CommentResponseDto.CommentListResponse>> getCommentsByBoardId(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "사용자 이메일 (좋아요 상태 확인용, 선택사항)", example = "user@university.ac.kr")
            @RequestParam(required = false) String email) {

        CommentResponseDto.CommentListResponse response = commentService.getCommentsByBoardId(boardId, email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}