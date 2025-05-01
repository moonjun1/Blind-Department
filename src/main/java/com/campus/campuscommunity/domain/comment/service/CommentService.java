package com.campus.campuscommunity.domain.comment.service;

import com.campus.campuscommunity.global.common.response.ResponseCode;
import com.campus.campuscommunity.global.config.exception.CustomException;
import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.board.repository.BoardRepository;
import com.campus.campuscommunity.domain.comment.dto.CommentRequestDto;
import com.campus.campuscommunity.domain.comment.dto.CommentResponseDto;
import com.campus.campuscommunity.domain.comment.entity.Comment;
import com.campus.campuscommunity.domain.comment.entity.CommentLike;
import com.campus.campuscommunity.domain.comment.repository.CommentLikeRepository;
import com.campus.campuscommunity.domain.comment.repository.CommentRepository;
import com.campus.campuscommunity.domain.user.entity.User;
import com.campus.campuscommunity.domain.user.repository.UserRepository;
import com.campus.campuscommunity.global.common.response.ResponseCode;
import com.campus.campuscommunity.global.config.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 작성
     * @param email 작성자 이메일
     * @param request 댓글 작성 요청 정보
     * @return 작성된 댓글 정보
     */
    public CommentResponseDto.CommentActionResponse createComment(String email, CommentRequestDto.CreateRequest request) {
        log.info("댓글 작성 요청: 이메일={}, 게시글ID={}, 부모댓글ID={}",
                email, request.getBoardId(), request.getParentId());

        // 1. 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        // 학과 인증 여부 확인
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 댓글 작성 시도: 이메일={}", email);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 2. 게시글 정보 조회
        Board board = boardRepository.findActiveById(request.getBoardId())
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 3. 부모 댓글 정보 조회 (대댓글인 경우)
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findActiveById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "부모 댓글을 찾을 수 없습니다."));

            // 3-1. 부모 댓글과 게시글 일치 여부 확인
            if (!parent.getBoard().getId().equals(board.getId())) {
                throw new CustomException(ResponseCode.BAD_REQUEST, "부모 댓글과 게시글이 일치하지 않습니다.");
            }
        }

        // 4. 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .content(request.getContent())
                .board(board)
                .writer(user)
                .writerDepartment(user.getDepartment()) // 학과만 표시
                .parent(parent)
                .likeCount(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 5. 댓글 저장
        Comment savedComment = commentRepository.save(comment);

        // 6. 게시글의 댓글 수 증가
        board.increaseCommentCount();
        boardRepository.save(board);

        log.info("댓글 작성 완료: id={}, 게시글 댓글 수={}", savedComment.getId(), board.getCommentCount());

        // 7. 응답 DTO 변환 후 반환
        return CommentResponseDto.CommentActionResponse.from(savedComment, false);
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 ID
     * @param email 수정자 이메일
     * @param request 댓글 수정 요청 정보
     * @return 수정된 댓글 정보
     */
    public CommentResponseDto.CommentActionResponse updateComment(Long commentId, String email, CommentRequestDto.UpdateRequest request) {
        log.info("댓글 수정 요청: id={}, 이메일={}", commentId, email);

        // 1. 댓글 조회
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 학과 인증 여부 확인
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 댓글 수정 시도: 이메일={}, 댓글ID={}", email, commentId);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 3. 권한 검증
        validateCommentOwnership(comment, user);

        // 4. 댓글 내용 수정
        comment.update(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("댓글 수정 완료: id={}", updatedComment.getId());

        // 5. 사용자의 좋아요 여부 확인
        boolean liked = commentLikeRepository.existsByCommentAndUser(updatedComment, user);

        // 6. 응답 DTO 변환 후 반환
        return CommentResponseDto.CommentActionResponse.from(updatedComment, liked);
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param email 삭제자 이메일
     */
    public void deleteComment(Long commentId, String email) {
        log.info("댓글 삭제 요청: id={}, 이메일={}", commentId, email);

        // 1. 댓글 조회
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 3. 권한 검증
        validateCommentOwnership(comment, user);

        // 4. 댓글 소프트 딜리트
        comment.delete();
        commentRepository.save(comment);

        // 5. 게시글의 댓글 수 감소
        Board board = comment.getBoard();
        board.decreaseCommentCount();
        boardRepository.save(board);

        log.info("댓글 삭제 완료: id={}, 게시글 댓글 수={}", commentId, board.getCommentCount());
    }

    /**
     * 댓글 좋아요 토글
     * @param commentId 댓글 ID
     * @param email 사용자 이메일
     * @return 업데이트된 댓글 정보
     */
    public CommentResponseDto.CommentActionResponse toggleLike(Long commentId, String email) {
        log.info("댓글 좋아요 요청: id={}, 이메일={}", commentId, email);

        // 1. 댓글 조회
        Comment comment = commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 학과 인증 여부 확인
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 댓글 좋아요 시도: 이메일={}, 댓글ID={}", email, commentId);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 3. 좋아요 여부 확인 및 처리
        boolean isLiked = processLikeToggle(comment, user);

        // 4. 응답 DTO 변환 후 반환
        return CommentResponseDto.CommentActionResponse.from(comment, isLiked);
    }

    /**
     * 좋아요 토글 처리
     * @param comment 댓글
     * @param user 사용자
     * @return 좋아요 상태 (true: 좋아요 함, false: 좋아요 취소)
     */
    private boolean processLikeToggle(Comment comment, User user) {
        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            // 이미 좋아요를 했으면 좋아요 취소
            CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user)
                    .orElseThrow(() -> new CustomException(ResponseCode.SERVER_ERROR, "좋아요 정보를 찾을 수 없습니다."));

            commentLikeRepository.delete(commentLike);
            comment.decreaseLikeCount();
            commentRepository.save(comment);
            log.debug("댓글 좋아요 취소: 댓글 id={}, 현재 좋아요 수={}", comment.getId(), comment.getLikeCount());
            return false;
        } else {
            // 좋아요 추가
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();

            commentLikeRepository.save(commentLike);
            comment.increaseLikeCount();
            commentRepository.save(comment);
            log.debug("댓글 좋아요 추가: 댓글 id={}, 현재 좋아요 수={}", comment.getId(), comment.getLikeCount());
            return true;
        }
    }

    /**
     * 게시글의 댓글 목록 조회
     * @param boardId 게시글 ID
     * @param email 조회자 이메일 (좋아요 상태 확인용, null 가능)
     * @return 댓글 목록 정보
     */
    @Transactional(readOnly = true)
    public CommentResponseDto.CommentListResponse getCommentsByBoardId(Long boardId, String email) {
        log.info("게시글 댓글 목록 조회: boardId={}, 조회자={}", boardId, email);

        // 1. 게시글 존재 여부 확인
        if (!boardRepository.existsById(boardId)) {
            throw new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }

        // 2. 일반 댓글만 조회 (대댓글 제외)
        List<Comment> topLevelComments = commentRepository.findActiveToplevelByBoardId(boardId);
        log.debug("일반 댓글 수: {}", topLevelComments.size());

        // 3. 모든 댓글 ID 수집
        List<Long> parentIds = topLevelComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 4. 대댓글 조회
        List<Comment> allReplies = new ArrayList<>();
        if (!parentIds.isEmpty()) {
            for (Long parentId : parentIds) {
                List<Comment> replies = commentRepository.findActiveRepliesByParentId(parentId);
                allReplies.addAll(replies);
            }
        }
        log.debug("대댓글 수: {}", allReplies.size());

        // 5. 사용자 조회 (좋아요 상태 확인용)
        User user = null;
        if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email).orElse(null);
        }

        // 6. 각 댓글의 좋아요 상태 및 대댓글 수 맵 생성
        Map<Long, Boolean> likeStatusMap = getLikeStatusMap(topLevelComments, allReplies, user);
        Map<Long, Integer> replyCountMap = getReplyCountMap(parentIds);

        // 7. 응답 DTO 변환 후 반환
        return CommentResponseDto.CommentListResponse.from(topLevelComments, allReplies, likeStatusMap, replyCountMap);
    }

    /**
     * 좋아요 상태 맵 생성
     * @param topLevelComments 일반 댓글 목록
     * @param allReplies 모든 대댓글 목록
     * @param user 사용자 (null 가능)
     * @return 댓글 ID -> 좋아요 여부 맵
     */
    private Map<Long, Boolean> getLikeStatusMap(List<Comment> topLevelComments, List<Comment> allReplies, User user) {
        Map<Long, Boolean> likeStatusMap = new HashMap<>();

        if (user == null) {
            return likeStatusMap;
        }

        // 모든 댓글 ID 수집
        List<Long> allCommentIds = new ArrayList<>();
        allCommentIds.addAll(topLevelComments.stream().map(Comment::getId).collect(Collectors.toList()));
        allCommentIds.addAll(allReplies.stream().map(Comment::getId).collect(Collectors.toList()));

        // 각 댓글의 좋아요 상태 확인
        for (Long commentId : allCommentIds) {
            commentRepository.findById(commentId).ifPresent(comment ->
                    likeStatusMap.put(commentId, commentLikeRepository.existsByCommentAndUser(comment, user))
            );
        }

        return likeStatusMap;
    }

    /**
     * 대댓글 수 맵 생성
     * @param parentIds 부모 댓글 ID 목록
     * @return 부모 댓글 ID -> 대댓글 수 맵
     */
    private Map<Long, Integer> getReplyCountMap(List<Long> parentIds) {
        Map<Long, Integer> replyCountMap = new HashMap<>();

        for (Long parentId : parentIds) {
            int replyCount = commentRepository.countActiveRepliesByParentId(parentId).intValue();
            replyCountMap.put(parentId, replyCount);
        }

        return replyCountMap;
    }

    /**
     * 댓글 소유권 검증
     * @param comment 댓글
     * @param user 사용자
     * @throws CustomException 댓글의 작성자가 아닌 경우 발생
     */
    private void validateCommentOwnership(Comment comment, User user) {
        if (!comment.getWriter().getId().equals(user.getId())) {
            log.warn("댓글 접근 권한 없음: 댓글 id={}, 요청자 id={}, 작성자 id={}",
                    comment.getId(), user.getId(), comment.getWriter().getId());
            throw new CustomException(ResponseCode.FORBIDDEN, "댓글에 대한 권한이 없습니다.");
        }
    }
}