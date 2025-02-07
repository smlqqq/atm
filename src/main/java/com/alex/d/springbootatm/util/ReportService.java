package com.alex.d.springbootatm.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReportService {

    ResponseEntity<?> createReport(String fileName);

    void autoSizeColumns(Sheet sheet, int columnCount);

    void addHeaders(Sheet sheet, String... headers);

    <T> ResponseEntity<?> reportConfig(String fileName, String sheetName, List<T> methods, String... headers);
}
