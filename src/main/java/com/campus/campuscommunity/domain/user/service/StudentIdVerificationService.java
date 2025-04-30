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
            "컴퓨터공학", "컴퓨터공학과", "컴퓨터공학부", "컴퓨터학과",
            "전자공학", "전자공학과", "전자공학부",
            "기계공학", "기계공학과", "기계공학부",
            "경영학", "경영학과", "경영학부"
            // 필요한 학과 추가
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