package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.report.SurveyReportResponse;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {
    private final AnswerRepository answerRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    private void createHeaderRow(Sheet sheet, String[] columnNames) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            headerRow.createCell(i).setCellValue(columnNames[i]);
        }
    }

    public ResponseEntity<byte[]> generateReport(Long reportId, Long surveyId, Principal principal) {
        try {
            return switch (reportId.intValue()) {
                case 1 -> generateSurveyAnswersReport(surveyId, principal);
                case 2 -> generateUserParticipationReport(principal);
                case 3 -> generateResponseTrendsReport(surveyId, principal);
                case 4 -> generateActiveUsersReport(principal);
                case 5 -> generatePopularSurveysReport(principal);
                default -> throw new IllegalArgumentException("Invalid report ID");
            };
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating report", e);
        }
    }

    public ResponseEntity<byte[]> generateSurveyAnswersReport(Long surveyId, Principal principal) {
        try {
            String email = principal.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<SurveyReportResponse> responses = answerRepository.findByAnswerBySurveyIdAndUserId(surveyId, user.getId());

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Survey Responses Report");

            String[] columnNames = {"User ID", "User Name", "Question ID", "Question Text", "Answer ID", "Answer Text"};
            createHeaderRow(sheet, columnNames);

            int rowIdx = 1;
            for (SurveyReportResponse response : responses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(response.userId());
                row.createCell(1).setCellValue(response.userName());
                row.createCell(2).setCellValue(response.questionId());
                row.createCell(3).setCellValue(response.questionText());
                row.createCell(4).setCellValue(response.answerId());
                row.createCell(5).setCellValue(response.answerText());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "survey_answers_report.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating survey answers report", e);
        }
    }

    private ResponseEntity<byte[]> generateUserParticipationReport(Principal principal) throws IOException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ParticipationResponse> responses = participationRepository.findAllUserParticipationsByUserId(user.getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Participation Report");

        String[] columnNames = {"User ID", "User Name", "Survey ID", "Survey Title", "Participation Date"};
        createHeaderRow(sheet, columnNames);

        int rowIdx = 1;
        for (ParticipationResponse response : responses) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(response.userId());
            row.createCell(1).setCellValue(response.username());
            row.createCell(2).setCellValue(response.surveyId());
            row.createCell(3).setCellValue(response.surveyTitle());
            row.createCell(4).setCellValue(response.participatedDate().toString());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "user_participation_report.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    private ResponseEntity<byte[]> generateResponseTrendsReport(Long surveyId, Principal principal) throws IOException {
        return null;
    }

    private ResponseEntity<byte[]> generateActiveUsersReport(Principal principal) throws IOException {
        return null;
    }

    private ResponseEntity<byte[]> generatePopularSurveysReport(Principal principal) throws IOException {
        return null;
    }
}
