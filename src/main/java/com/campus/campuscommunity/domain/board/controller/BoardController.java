package com.campus.campuscommunity.domain.board.controller;

import com.campus.campuscommunity.common.response.ApiResponse;
import com.campus.campuscommunity.domain.board.dto.BoardRequestDto;
import com.campus.campuscommunity.domain.board.dto.BoardResponseDto;
import com.campus.campuscommunity.domain.board.entity.BoardCategory;
import com.campus.campuscommunity.domain.board.service.BoardService;
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
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "게시판 관리", description = "게시글 작성, 조회, 수정, 삭제 및 좋아요 API")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 작성 API
     * POST /api/boards
     */
    @Operation(
            summary = "게시글 작성",
            description = "새로운 게시글을 작성합니다. 제목, 내용, 카테고리 정보가 필요합니다. 작성자는 익명으로 학과만 표시됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "게시글 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 작성 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 201,\n" +
                                            "  \"message\": \"게시글이 성공적으로 작성되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"첫 번째 게시글입니다\",\n" +
                                            "    \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다.\",\n" +
                                            "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "    \"writerId\": 1,\n" +
                                            "    \"category\": \"FREE\",\n" +
                                            "    \"categoryDisplayName\": \"자유게시판\",\n" +
                                            "    \"viewCount\": 0,\n" +
                                            "    \"likeCount\": 0,\n" +
                                            "    \"dislikeCount\": 0,\n" +
                                            "    \"createdAt\": \"2025-04-30T01:20:00\",\n" +
                                            "    \"updatedAt\": \"2025-04-30T01:20:00\",\n" +
                                            "    \"likeStatus\": \"NONE\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 오류)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "입력값 오류 응답",
                                    value = "{\n" +
                                            "  \"status\": 400,\n" +
                                            "  \"message\": \"잘못된 요청입니다.\",\n" +
                                            "  \"errors\": [\n" +
                                            "    {\n" +
                                            "      \"field\": \"title\",\n" +
                                            "      \"value\": \"\",\n" +
                                            "      \"reason\": \"제목은 필수 입력값입니다.\"\n" +
                                            "    }\n" +
                                            "  ]\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패 응답",
                                    value = "{\n" +
                                            "  \"status\": 401,\n" +
                                            "  \"message\": \"인증이 필요합니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardDetailResponse>> createBoard(
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email,
            @Parameter(description = "게시글 작성 정보", required = true)
            @Valid @RequestBody BoardRequestDto.CreateRequest request) {

        BoardResponseDto.BoardDetailResponse response = boardService.createBoard(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 성공적으로 작성되었습니다.", response));
    }

    /**
     * 게시글 상세 조회 API
     * GET /api/boards/{id}
     */
    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 ID로 게시글의 상세 정보를 조회합니다. 조회 시 조회수가 자동으로 증가합니다. 이메일 파라미터를 통해 해당 사용자의 좋아요/싫어요 상태도 함께 확인할 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 상세 조회 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"첫 번째 게시글입니다\",\n" +
                                            "    \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다.\",\n" +
                                            "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "    \"writerId\": 1,\n" +
                                            "    \"category\": \"FREE\",\n" +
                                            "    \"categoryDisplayName\": \"자유게시판\",\n" +
                                            "    \"viewCount\": 43,\n" +
                                            "    \"likeCount\": 15,\n" +
                                            "    \"dislikeCount\": 3,\n" +
                                            "    \"createdAt\": \"2025-04-15T14:30:00\",\n" +
                                            "    \"updatedAt\": \"2025-04-15T15:45:00\",\n" +
                                            "    \"likeStatus\": \"LIKE\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "게시글 없음 응답",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"message\": \"게시글을 찾을 수 없습니다.\"\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardDetailResponse>> getBoardDetail(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("id") Long boardId,
            @Parameter(description = "사용자 이메일 (좋아요 상태 확인용, 선택사항)", example = "user@university.ac.kr")
            @RequestParam(required = false) String email) {

        BoardResponseDto.BoardDetailResponse response = boardService.getBoardDetail(boardId, email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 목록 조회 API
     * GET /api/boards
     */
    @Operation(
            summary = "게시글 목록 조회",
            description = "게시글 목록을 조회합니다. 키워드, 카테고리, 학과별 필터링과 정렬 기능을 제공합니다. 페이징 처리도 지원합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardListResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 목록 조회 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"boards\": [\n" +
                                            "      {\n" +
                                            "        \"id\": 1,\n" +
                                            "        \"title\": \"첫 번째 게시글입니다\",\n" +
                                            "        \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "        \"category\": \"FREE\",\n" +
                                            "        \"categoryDisplayName\": \"자유게시판\",\n" +
                                            "        \"viewCount\": 42,\n" +
                                            "        \"likeCount\": 15,\n" +
                                            "        \"commentCount\": 7,\n" +
                                            "        \"createdAt\": \"2025-04-15T14:30:00\"\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"id\": 2,\n" +
                                            "        \"title\": \"스터디 모집합니다\",\n" +
                                            "        \"writerDepartment\": \"전자공학과\",\n" +
                                            "        \"category\": \"STUDY\",\n" +
                                            "        \"categoryDisplayName\": \"스터디\",\n" +
                                            "        \"viewCount\": 18,\n" +
                                            "        \"likeCount\": 5,\n" +
                                            "        \"commentCount\": 2,\n" +
                                            "        \"createdAt\": \"2025-04-15T11:20:00\"\n" +
                                            "      }\n" +
                                            "    ],\n" +
                                            "    \"totalPages\": 5,\n" +
                                            "    \"totalElements\": 42,\n" +
                                            "    \"currentPage\": 0\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardListResponse>> getBoardList(
            @Parameter(description = "검색 키워드", example = "스터디")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리", example = "FREE", schema = @Schema(implementation = BoardCategory.class))
            @RequestParam(required = false) BoardCategory category,
            @Parameter(description = "학과", example = "컴퓨터공학과")
            @RequestParam(required = false) String department,
            @Parameter(description = "정렬 기준 (created: 최신순, views: 조회수순, likes: 좋아요순)", example = "created")
            @RequestParam(defaultValue = "created") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        BoardRequestDto.SearchRequest request = BoardRequestDto.SearchRequest.builder()
                .keyword(keyword)
                .category(category)
                .department(department)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        BoardResponseDto.BoardListResponse response = boardService.getBoardList(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 수정 API
     * PUT /api/boards/{id}
     */
    @Operation(
            summary = "게시글 수정",
            description = "게시글을 수정합니다. 제목, 내용, 카테고리를 수정할 수 있습니다. 작성자만 수정 가능합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 수정 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"게시글이 성공적으로 수정되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"수정된 게시글 제목\",\n" +
                                            "    \"content\": \"게시글 내용을 수정했습니다. 더 자세한 내용을 추가했습니다.\",\n" +
                                            "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "    \"writerId\": 1,\n" +
                                            "    \"category\": \"QNA\",\n" +
                                            "    \"categoryDisplayName\": \"질문/답변\",\n" +
                                            "    \"viewCount\": 43,\n" +
                                            "    \"likeCount\": 15,\n" +
                                            "    \"dislikeCount\": 3,\n" +
                                            "    \"createdAt\": \"2025-04-15T14:30:00\",\n" +
                                            "    \"updatedAt\": \"2025-04-30T01:30:00\",\n" +
                                            "    \"likeStatus\": \"LIKE\"\n" +
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
                    description = "권한 없음 (작성자가 아님)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "권한 없음 응답",
                                    value = "{\n" +
                                            "  \"status\": 403,\n" +
                                            "  \"message\": \"게시글에 대한 권한이 없습니다.\"\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 없음"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardDetailResponse>> updateBoard(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("id") Long boardId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email,
            @Parameter(description = "게시글 수정 정보", required = true)
            @Valid @RequestBody BoardRequestDto.UpdateRequest request) {

        BoardResponseDto.BoardDetailResponse response = boardService.updateBoard(boardId, email, request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 수정되었습니다.", response));
    }

    /**
     * 게시글 삭제 API
     * DELETE /api/boards/{id}
     */
    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다. (소프트 딜리트) 작성자만 삭제 가능합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "게시글 삭제 성공"
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
                    description = "게시글 없음"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("id") Long boardId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email) {

        boardService.deleteBoard(boardId, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 좋아요 API
     * POST /api/boards/{id}/like
     */
    @Operation(
            summary = "게시글 좋아요",
            description = "게시글에 좋아요를 추가하거나 취소합니다. 이미 좋아요한 상태에서 다시 요청하면 좋아요가 취소됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "좋아요 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "좋아요 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"좋아요가 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"첫 번째 게시글입니다\",\n" +
                                            "    \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다.\",\n" +
                                            "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "    \"writerId\": 1,\n" +
                                            "    \"category\": \"FREE\",\n" +
                                            "    \"categoryDisplayName\": \"자유게시판\",\n" +
                                            "    \"viewCount\": 43,\n" +
                                            "    \"likeCount\": 16,\n" +
                                            "    \"dislikeCount\": 3,\n" +
                                            "    \"createdAt\": \"2025-04-15T14:30:00\",\n" +
                                            "    \"updatedAt\": \"2025-04-15T15:45:00\",\n" +
                                            "    \"likeStatus\": \"LIKE\"\n" +
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
                    description = "게시글 없음"
            )
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardDetailResponse>> likeBoard(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("id") Long boardId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email) {

        BoardResponseDto.BoardDetailResponse response = boardService.toggleLike(boardId, email, true);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 처리되었습니다.", response));
    }

    /**
     * 게시글 싫어요 API
     * POST /api/boards/{id}/dislike
     */
    @Operation(
            summary = "게시글 싫어요",
            description = "게시글에 싫어요를 추가하거나 취소합니다. 이미 싫어요한 상태에서 다시 요청하면 싫어요가 취소됩니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "싫어요 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoardResponseDto.BoardDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "싫어요 성공 응답",
                                    value = "{\n" +
                                            "  \"status\": 200,\n" +
                                            "  \"message\": \"싫어요가 처리되었습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"title\": \"첫 번째 게시글입니다\",\n" +
                                            "    \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다.\",\n" +
                                            "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                            "    \"writerId\": 1,\n" +
                                            "    \"category\": \"FREE\",\n" +
                                            "    \"categoryDisplayName\": \"자유게시판\",\n" +
                                            "    \"viewCount\": 43,\n" +
                                            "    \"likeCount\": 15,\n" +
                                            "    \"dislikeCount\": 4,\n" +
                                            "    \"createdAt\": \"2025-04-15T14:30:00\",\n" +
                                            "    \"updatedAt\": \"2025-04-15T15:45:00\",\n" +
                                            "    \"likeStatus\": \"DISLIKE\"\n" +
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
                    description = "게시글 없음"
            )
    })
    @PostMapping("/{id}/dislike")
    public ResponseEntity<ApiResponse<BoardResponseDto.BoardDetailResponse>> dislikeBoard(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable("id") Long boardId,
            @Parameter(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
            @RequestParam String email) {

        BoardResponseDto.BoardDetailResponse response = boardService.toggleLike(boardId, email, false);
        return ResponseEntity.ok(ApiResponse.success("싫어요가 처리되었습니다.", response));
    }
}