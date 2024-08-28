package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.report.PopularSurveyReportResponse;
import com.yourcompany.surveys.dto.report.ResponseTrendReportResponse;
import com.yourcompany.surveys.dto.report.SurveyReportResponse;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExcelReportService {
    private final AnswerRepository answerRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;

    private void createHeaderRow(Sheet sheet, String[] columnNames) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            headerRow.createCell(i).setCellValue(columnNames[i]);
        }
    }

    public ResponseEntity<byte[]> generateReport(Long reportId, Optional<Long> surveyId, Principal principal) {
        try {
            return switch (reportId.intValue()) {
                case 1 -> generateSurveyAnswersReport(surveyId.orElseThrow(() -> new IllegalArgumentException("Survey ID is required")), principal);
                case 2 -> generateUserParticipationReport(principal);
                case 3 -> generateResponseTrendsReport(surveyId.orElseThrow(() -> new IllegalArgumentException("Survey ID is required")), principal);
                case 4 -> generatePopularSurveysReport(principal);
                case 5 -> generateUserParticipationBySurveyReport(surveyId.orElseThrow(() -> new IllegalArgumentException("Survey ID is required")), principal);
                case 6 -> generateUserSatisfactionReport(principal);
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
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ResponseTrendReportResponse> responseTrends = answerRepository.findResponseTrendsBySurveyIdAndUserId(surveyId, user.getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Response Trends Report");

        String[] columnNames = {"Question ID", "Question Text", "Answer Text", "Frequency"};
        createHeaderRow(sheet, columnNames);

        int rowIdx = 1;
        for (ResponseTrendReportResponse trend : responseTrends) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(trend.questionId());
            row.createCell(1).setCellValue(trend.questionText());
            row.createCell(2).setCellValue(trend.answerText());
            row.createCell(3).setCellValue(trend.frequency());
        }

        for (int i = 0; i < columnNames.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "response_trends_report.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    private ResponseEntity<byte[]> generatePopularSurveysReport(Principal principal) throws IOException {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PopularSurveyReportResponse> popularSurveys = surveyRepository.findPopularSurveysByUserId(user.getId());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Popular Surveys Report");

        String[] columnNames = {"Survey ID", "Survey Title", "Participation Count"};
        createHeaderRow(sheet, columnNames);

        int rowIdx = 1;
        for (PopularSurveyReportResponse survey : popularSurveys) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(survey.surveyId());
            row.createCell(1).setCellValue(survey.surveyTitle());
            row.createCell(2).setCellValue(survey.participationCount());
        }

        for (int i = 0; i < columnNames.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "popular_surveys_report.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
    private ResponseEntity<byte[]> generateUserParticipationBySurveyReport(Long surveyId, Principal principal) {
        return null;
    }

    private ResponseEntity<byte[]> generateUserSatisfactionReport(Principal principal) {
        return null;
    }
}
