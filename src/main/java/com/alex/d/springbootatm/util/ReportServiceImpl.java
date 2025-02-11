package com.alex.d.springbootatm.util;

import com.alex.d.springbootatm.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public ResponseEntity<?> createReport(String fileName) {
        File file = new File(fileName);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }


    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @Override
    public List<String> createHeaders(String... headers) {
        return Arrays.asList(headers);
    }


    private void addHeaders(Sheet sheet, List<String> headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }
    }


    private void fillRowFromObject(Row row, Object item, List<Field> fields) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(item);
                row.createCell(i).setCellValue(value != null ? value.toString() : "null");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field: " + field.getName(), e);
            }
        }
    }

    private void fillRowWithNulls(Row row, int columnsCount) {
        for (int i = 0; i < columnsCount; i++) {
            row.createCell(i).setCellValue("null");
        }
    }

    @Override
    public <T> ResponseEntity<?> reportConfig(String fileName, String sheetName, List<T> objectData, List<String> headers) {

        if (objectData.isEmpty()) {
            log.error("No object data found");
            ErrorResponse errorResponse = new ErrorResponse(Instant.now(), "404", "No object data found", "/cards/export/excel");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetName);

            addHeaders(sheet, headers);

            int rowNum = 1;

            // TODO нулевые значения объекта выбрасывают исключение IndexOutOfBoundException
            Class<?> dtoClass = objectData.get(0).getClass();
            List<Field> fields = Arrays.asList(dtoClass.getDeclaredFields());

            for (T item : objectData) {
                Row row = sheet.createRow(rowNum++);
                fillRowFromObject(row, item, fields);
            }

            autoSizeColumns(sheet, headers.size());

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Report successfully created {}", fileName);
        return createReport(fileName);
    }

}
