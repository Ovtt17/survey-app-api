package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.report.SurveyReportResponse;
import com.yourcompany.surveys.repository.AnswerRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {
    private final AnswerRepository answerRepository;

    public ResponseEntity<byte[]> generateSurveyAnswersReport(Long surveyId) {
        try {
            List<SurveyReportResponse> responses = answerRepository.findByAnswerBySurveyId(surveyId);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Survey Responses Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Question ID");
            headerRow.createCell(1).setCellValue("Question Text");
            headerRow.createCell(2).setCellValue("Answer ID");
            headerRow.createCell(3).setCellValue("Answer Text");
            headerRow.createCell(4).setCellValue("User ID");
            headerRow.createCell(5).setCellValue("User Name");

            int rowIdx = 1;
            for (SurveyReportResponse response : responses) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(response.questionId());
                row.createCell(1).setCellValue(response.questionText());
                row.createCell(2).setCellValue(response.answerId());
                row.createCell(3).setCellValue(response.answerText());
                row.createCell(4).setCellValue(response.userId());
                row.createCell(5).setCellValue(response.userName());
            }

            // Ajustar el ancho de las columnas
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
}
