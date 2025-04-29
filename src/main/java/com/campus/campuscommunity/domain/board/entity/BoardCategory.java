package com.campus.campuscommunity.domain.board.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게시글 카테고리를 정의하는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum BoardCategory {
    // 학과별 카테고리
    COMPUTER_SCIENCE("컴퓨터공학과", "학과별"),
    ELECTRONICS("전자공학과", "학과별"),
    MECHANICAL("기계공학과", "학과별"),
    MANAGEMENT("경영학과", "학과별"),

    // 주제별 카테고리
    CAREER("취업/진로", "주제별"),
    STUDY("스터디", "주제별"),
    CLUB("동아리", "주제별"),
    CAMPUS_LIFE("대학생활", "주제별"),
    FREE("자유게시판", "주제별"),
    QNA("질문/답변", "주제별");

    private final String displayName; // 표시 이름
    private final String group;       // 카테고리 그룹 (학과별/주제별)
}