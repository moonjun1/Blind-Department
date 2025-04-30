package com.campus.campuscommunity.domain.board.service;

import com.campus.campuscommunity.global.common.response.ResponseCode;
import com.campus.campuscommunity.global.config.exception.CustomException;
import com.campus.campuscommunity.domain.board.dto.BoardRequestDto;
import com.campus.campuscommunity.domain.board.dto.BoardResponseDto;
import com.campus.campuscommunity.domain.board.entity.Board;
import com.campus.campuscommunity.domain.board.entity.BoardLike;
import com.campus.campuscommunity.domain.board.repository.BoardLikeRepository;
import com.campus.campuscommunity.domain.board.repository.BoardRepository;
import com.campus.campuscommunity.domain.user.entity.User;
import com.campus.campuscommunity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 게시판 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 생성
     * @param email 작성자 이메일
     * @param request 게시글 작성 요청 정보
     * @return 생성된 게시글 상세 정보
     */
    public BoardResponseDto.BoardDetailResponse createBoard(String email, BoardRequestDto.CreateRequest request) {
        log.info("게시글 생성 요청: 이메일={}, 제목={}", email, request.getTitle());

        // 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        // 학과 인증 여부 확인 - 이 부분 추가
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 게시글 작성 시도: 이메일={}", email);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 게시글 엔티티 생성
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(user)
                .writerDepartment(user.getDepartment()) // 학과만 표시
                .category(request.getCategory())
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .isDeleted(false)
                .createdAt(now)    // 생성 시간 설정
                .updatedAt(now)    // 수정 시간 설정
                .build();

        // 게시글 저장
        Board savedBoard = boardRepository.save(board);
        log.info("게시글 생성 완료: id={}", savedBoard.getId());

        // 응답 DTO 변환 후 반환
        return BoardResponseDto.BoardDetailResponse.from(savedBoard);
    }
    /**
     * 게시글 상세 조회
     * @param boardId 게시글 ID
     * @param email 조회자 이메일 (좋아요 상태 확인용, null 가능)
     * @return 게시글 상세 정보
     */
    @Transactional
    public BoardResponseDto.BoardDetailResponse getBoardDetail(Long boardId, String email) {
        log.info("게시글 상세 조회: id={}, 조회자={}", boardId, email);

        // 게시글 조회
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 조회수 증가
        board.increaseViewCount();
        boardRepository.save(board);
        log.debug("게시글 조회수 증가: id={}, 현재 조회수={}", boardId, board.getViewCount());

        // 사용자의 좋아요 상태 확인
        String likeStatus = getLikeStatus(board, email);

        // 응답 DTO 변환 후 반환
        return BoardResponseDto.BoardDetailResponse.from(board, likeStatus);
    }

    /**
     * 게시글 목록 조회
     * @param request 검색 요청 정보
     * @return 게시글 목록 정보
     */
    @Transactional(readOnly = true)
    public BoardResponseDto.BoardListResponse getBoardList(BoardRequestDto.SearchRequest request) {
        log.info("게시글 목록 조회: 키워드={}, 카테고리={}, 학과={}, 정렬={}",
                request.getKeyword(), request.getCategory(), request.getDepartment(), request.getSort());

        // 페이징 및 정렬 설정
        Pageable pageable = createPageable(request);
        Page<Board> boardPage;

        // 검색 조건에 따른 게시글 조회
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            // 키워드 검색
            boardPage = boardRepository.searchByKeyword(request.getKeyword(), pageable);
            log.debug("키워드 검색 결과: 총 {}건", boardPage.getTotalElements());
        } else if (request.getCategory() != null) {
            // 카테고리별 조회
            boardPage = boardRepository.findByIsDeletedFalseAndCategoryOrderByCreatedAtDesc(request.getCategory(), pageable);
            log.debug("카테고리별 조회 결과: 총 {}건", boardPage.getTotalElements());
        } else if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            // 학과별 조회
            boardPage = boardRepository.findByIsDeletedFalseAndWriterDepartmentOrderByCreatedAtDesc(request.getDepartment(), pageable);
            log.debug("학과별 조회 결과: 총 {}건", boardPage.getTotalElements());
        } else {
            // 전체 조회
            boardPage = boardRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
            log.debug("전체 조회 결과: 총 {}건", boardPage.getTotalElements());
        }

        // 응답 DTO 변환 후 반환
        return BoardResponseDto.BoardListResponse.builder()
                .boards(BoardResponseDto.BoardSummary.fromList(boardPage.getContent()))
                .totalPages(boardPage.getTotalPages())
                .totalElements(boardPage.getTotalElements())
                .currentPage(boardPage.getNumber())
                .build();
    }

    /**
     * 게시글 수정
     * @param boardId 게시글 ID
     * @param email 수정자 이메일
     * @param request 게시글 수정 요청 정보
     * @return 수정된 게시글 상세 정보
     */
    public BoardResponseDto.BoardDetailResponse updateBoard(Long boardId, String email, BoardRequestDto.UpdateRequest request) {
        log.info("게시글 수정 요청: id={}, 이메일={}", boardId, email);

        // 게시글 조회
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 작성자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 학과 인증 여부 확인
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 게시글 수정 시도: 이메일={}, 게시글ID={}", email, boardId);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 권한 검증
        validateBoardOwnership(board, user);

        // 게시글 내용 수정
        board.update(request.getTitle(), request.getContent(), request.getCategory());
        Board updatedBoard = boardRepository.save(board);
        log.info("게시글 수정 완료: id={}", updatedBoard.getId());

        // 응답 DTO 변환 후 반환
        return BoardResponseDto.BoardDetailResponse.from(updatedBoard);
    }

    /**
     * 게시글 삭제 (소프트 딜리트)
     * @param boardId 게시글 ID
     * @param email 삭제자 이메일
     */
    public void deleteBoard(Long boardId, String email) {
        log.info("게시글 삭제 요청: id={}, 이메일={}", boardId, email);

        // 게시글 조회
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 작성자 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 권한 검증
        validateBoardOwnership(board, user);

        // 게시글 소프트 딜리트
        board.delete();
        boardRepository.save(board);
        log.info("게시글 삭제 완료: id={}", boardId);
    }

    /**
     * 게시글 좋아요/싫어요 처리
     * @param boardId 게시글 ID
     * @param email 사용자 이메일
     * @param isLike true: 좋아요, false: 싫어요
     * @return 업데이트된 게시글 상세 정보
     */
    public BoardResponseDto.BoardDetailResponse toggleLike(Long boardId, String email, boolean isLike) {
        log.info("게시글 좋아요/싫어요 요청: id={}, 이메일={}, 좋아요={}", boardId, email, isLike);

        // 게시글 조회
        Board board = boardRepository.findActiveById(boardId)
                .orElseThrow(() -> new CustomException(ResponseCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        // 학과 인증 여부 확인
        if (!user.isVerified()) {
            log.warn("인증되지 않은 사용자의 게시글 좋아요/싫어요 시도: 이메일={}, 게시글ID={}", email, boardId);
            throw new CustomException(ResponseCode.DEPARTMENT_NOT_VERIFIED);
        }

        // 현재 좋아요/싫어요 상태 확인
        Optional<BoardLike> boardLikeOpt = boardLikeRepository.findByBoardAndUser(board, user);
        BoardLike.LikeStatus newStatus = isLike ? BoardLike.LikeStatus.LIKE : BoardLike.LikeStatus.DISLIKE;

        if (boardLikeOpt.isPresent()) {
            // 기존 좋아요/싫어요가 있는 경우
            return processExistingLike(board, boardLikeOpt.get(), newStatus);
        } else {
            // 새로운 좋아요/싫어요인 경우
            return processNewLike(board, user, newStatus);
        }
    }

    /**
     * 기존 좋아요/싫어요 처리
     * @param board 게시글
     * @param boardLike 기존 좋아요/싫어요 정보
     * @param newStatus 새로운 상태
     * @return 게시글 상세 정보
     */
    private BoardResponseDto.BoardDetailResponse processExistingLike(Board board, BoardLike boardLike, BoardLike.LikeStatus newStatus) {
        BoardLike.LikeStatus oldStatus = boardLike.getStatus();

        // 같은 상태면 취소
        if (oldStatus == newStatus) {
            // 기존 상태 취소
            if (oldStatus == BoardLike.LikeStatus.LIKE) {
                board.decreaseLikeCount();
                log.debug("좋아요 취소: 게시글 id={}, 현재 좋아요 수={}", board.getId(), board.getLikeCount());
            } else {
                board.decreaseDislikeCount();
                log.debug("싫어요 취소: 게시글 id={}, 현재 싫어요 수={}", board.getId(), board.getDislikeCount());
            }

            // 좋아요 기록 삭제
            boardLikeRepository.delete(boardLike);
            boardRepository.save(board);
            return BoardResponseDto.BoardDetailResponse.from(board, "NONE");
        } else {
            // 다른 상태면 변경
            // 기존 상태 취소
            if (oldStatus == BoardLike.LikeStatus.LIKE) {
                board.decreaseLikeCount();
                log.debug("좋아요 -> 싫어요 변경: 좋아요 취소, 게시글 id={}, 현재 좋아요 수={}", board.getId(), board.getLikeCount());
            } else {
                board.decreaseDislikeCount();
                log.debug("싫어요 -> 좋아요 변경: 싫어요 취소, 게시글 id={}, 현재 싫어요 수={}", board.getId(), board.getDislikeCount());
            }

            // 새 상태 적용
            if (newStatus == BoardLike.LikeStatus.LIKE) {
                board.increaseLikeCount();
                log.debug("좋아요 추가: 게시글 id={}, 현재 좋아요 수={}", board.getId(), board.getLikeCount());
            } else {
                board.increaseDislikeCount();
                log.debug("싫어요 추가: 게시글 id={}, 현재 싫어요 수={}", board.getId(), board.getDislikeCount());
            }

            // 상태 변경
            boardLike.changeStatus(newStatus);
            boardLikeRepository.save(boardLike);
            boardRepository.save(board);
            return BoardResponseDto.BoardDetailResponse.from(board, newStatus.name());
        }
    }

    /**
     * 새로운 좋아요/싫어요 처리
     * @param board 게시글
     * @param user 사용자
     * @param newStatus 새로운 상태
     * @return 게시글 상세 정보
     */
    private BoardResponseDto.BoardDetailResponse processNewLike(Board board, User user, BoardLike.LikeStatus newStatus) {
        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 새로 좋아요/싫어요 생성
        BoardLike boardLike = BoardLike.builder()
                .board(board)
                .user(user)
                .status(newStatus)
                .createdAt(now)  // 명시적으로 생성 시간 설정
                .build();

        // 카운트 증가
        if (newStatus == BoardLike.LikeStatus.LIKE) {
            board.increaseLikeCount();
            log.debug("새 좋아요 추가: 게시글 id={}, 현재 좋아요 수={}", board.getId(), board.getLikeCount());
        } else {
            board.increaseDislikeCount();
            log.debug("새 싫어요 추가: 게시글 id={}, 현재 싫어요 수={}", board.getId(), board.getDislikeCount());
        }

        boardLikeRepository.save(boardLike);
        boardRepository.save(board);
        return BoardResponseDto.BoardDetailResponse.from(board, newStatus.name());
    }

    /**
     * 게시글에 대한 사용자의 좋아요 상태 확인
     * @param board 게시글
     * @param email 사용자 이메일
     * @return 좋아요 상태 (NONE, LIKE, DISLIKE)
     */
    private String getLikeStatus(Board board, String email) {
        if (email == null) {
            return "NONE";
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "NONE";
        }

        Optional<BoardLike> boardLike = boardLikeRepository.findByBoardAndUser(board, user);
        return boardLike.map(like -> like.getStatus().name()).orElse("NONE");
    }

    /**
     * 게시글 소유권 검증
     * @param board 게시글
     * @param user 사용자
     * @throws CustomException 게시글의 작성자가 아닌 경우 발생
     */
    private void validateBoardOwnership(Board board, User user) {
        if (!board.getWriter().getId().equals(user.getId())) {
            log.warn("게시글 접근 권한 없음: 게시글 id={}, 요청자 id={}, 작성자 id={}",
                    board.getId(), user.getId(), board.getWriter().getId());
            throw new CustomException(ResponseCode.FORBIDDEN, "게시글에 대한 권한이 없습니다.");
        }
    }

    /**
     * 페이징 및 정렬 설정 생성
     * @param request 검색 요청 정보
     * @return 페이징 및 정렬 설정
     */
    private Pageable createPageable(BoardRequestDto.SearchRequest request) {
        Sort sort;

        switch (request.getSort()) {
            case "views":
                sort = Sort.by(Sort.Direction.DESC, "viewCount");
                break;
            case "likes":
                sort = Sort.by(Sort.Direction.DESC, "likeCount");
                break;
            case "created":
            default:
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}