package com.campus.campuscommunity.domain.user.service;

import com.campus.campuscommunity.global.config.ocr.GoogleVisionOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentIdVerificationService {

    private final GoogleVisionOcrService ocrService;

    // 대학 학과 목록
    private final List<String> departmentList = Arrays.asList(
            // 공학 계열
            "컴퓨터공학", "컴퓨터공학과", "컴퓨터공학부", "컴퓨터학과", "소프트웨어학과", "정보통신공학과", "인공지능학과",
            "데이터사이언스학과", "사이버보안학과", "소프트웨어융합학과", "빅데이터학과", "IT융합학과",
            "전자공학", "전자공학과", "전자공학부", "전기전자공학과", "전자정보공학과", "전기공학과",
            "기계공학", "기계공학과", "기계공학부", "기계설계공학과", "항공우주공학과", "조선해양공학과", "로봇공학과",
            "화학공학", "화학공학과", "화학공학부", "신소재공학과", "재료공학과", "고분자공학과",
            "건축공학", "건축공학과", "건축학과", "도시공학과", "토목공학과", "환경공학과", "조경학과", "산업공학과","ai빅데이터학과",

            // 자연과학 계열
            "수학", "수학과", "통계학", "통계학과", "물리학", "물리학과", "화학", "화학과",
            "생물학", "생물학과", "생명과학", "생명과학과", "지구과학", "지구과학과", "천문학", "천문학과",
            "대기과학", "대기과학과", "해양학", "해양학과", "지질학", "지질학과",

            // 경영/경제 계열
            "경영학", "경영학과", "경영학부", "경제학", "경제학과", "경제학부", "무역학", "무역학과",
            "회계학", "회계학과", "국제통상학", "국제통상학과", "금융학", "금융학과", "세무학", "세무학과",
            "부동산학", "부동산학과", "물류학", "물류학과", "경영정보학", "경영정보학과", "관광경영학과",

            // 인문 계열
            "국어국문학", "국어국문학과", "영어영문학", "영어영문학과", "불어불문학", "불어불문학과",
            "독어독문학", "독어독문학과", "중어중문학", "중어중문학과", "일어일문학", "일어일문학과",
            "사학", "사학과", "철학", "철학과", "종교학", "종교학과", "문헌정보학", "문헌정보학과",
            "언어학", "언어학과", "고고학", "고고학과", "문예창작학", "문예창작학과",

            // 사회과학 계열
            "사회학", "사회학과", "심리학", "심리학과", "정치외교학", "정치외교학과", "행정학", "행정학과",
            "언론정보학", "언론정보학과", "사회복지학", "사회복지학과", "인류학", "인류학과",
            "지리학", "지리학과", "문화인류학", "문화인류학과", "국제관계학", "국제관계학과",

            // 의학/보건 계열
            "의학", "의학과", "의예과", "치의학", "치의학과", "치예과", "한의학", "한의학과", "한의예과",
            "약학", "약학과", "간호학", "간호학과", "물리치료학", "물리치료학과", "작업치료학", "작업치료학과",
            "임상병리학", "임상병리학과", "방사선학", "방사선학과", "치위생학", "치위생학과",
            "보건학", "보건학과", "보건행정학", "보건행정학과", "의료정보학", "의료정보학과",

            // 교육 계열
            "교육학", "교육학과", "유아교육학", "유아교육학과", "초등교육학", "초등교육학과",
            "교육공학", "교육공학과", "특수교육학", "특수교육학과", "국어교육", "국어교육과",
            "영어교육", "영어교육과", "수학교육", "수학교육과", "과학교육", "과학교육과",
            "체육교육", "체육교육과", "음악교육", "음악교육과", "미술교육", "미술교육과",

            // 예체능 계열
            "미술", "미술학과", "음악", "음악학과", "체육", "체육학과", "무용", "무용학과",
            "디자인", "디자인학과", "시각디자인", "시각디자인학과", "산업디자인", "산업디자인학과",
            "패션디자인", "패션디자인학과", "실내디자인", "실내디자인학과", "공예", "공예학과",
            "연극", "연극학과", "영화", "영화학과", "애니메이션", "애니메이션학과", "게임", "게임학과",
            "만화", "만화학과", "사진", "사진학과", "방송연예", "방송연예학과",

            // 농수산/생활과학 계열
            "농학", "농학과", "원예학", "원예학과", "산림학", "산림학과", "조경학", "조경학과",
            "식품공학", "식품공학과", "수산학", "수산학과", "해양학", "해양학과", "축산학", "축산학과",
            "식품영양학", "식품영양학과", "의류학", "의류학과", "주거환경학", "주거환경학과",
            "소비자학", "소비자학과", "가족학", "가족학과", "아동학", "아동학과",

            // 군사/경찰/소방 계열
            "군사학", "군사학과", "국방학", "국방학과", "경찰행정학", "경찰행정학과", "범죄수사학", "범죄수사학과",
            "소방방재학", "소방방재학과", "안전공학", "안전공학과", "응급구조학", "응급구조학과"
            ,"산업경영공학과"
    );

    public String verifyStudentIdCard(MultipartFile imageFile) throws IOException {
        // OCR로 텍스트 추출
        String extractedText = ocrService.extractText(imageFile);
        log.info("추출된 텍스트: {}", extractedText);

        // 추출된 텍스트에서 학과 식별
        String detectedDepartment = detectDepartment(extractedText);

        if (detectedDepartment == null) {
            log.warn("학과 정보를 찾을 수 없습니다: {}", extractedText);
            throw new IllegalArgumentException("학생증에서 학과 정보를 찾을 수 없습니다.");
        }

        log.info("인식된 학과: {}", detectedDepartment);
        return detectedDepartment;
    }

    private String detectDepartment(String text) {
        // 1. 학과 목록에서 직접 매칭
        for (String department : departmentList) {
            if (text.contains(department)) {
                return department;
            }
        }

        // 2. 정규식 패턴으로 찾기
        Pattern pattern = Pattern.compile("(?:학과|전공|소속)[:\\s]*([가-힣a-zA-Z\\s]+(?:학과|공학|전공|학부))");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }
}