package com.alex.d.springbootatm.util;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReportService {

    ResponseEntity<?> createReport(String fileName);

    List<String> createHeaders(String... headers);

    <T> ResponseEntity<?> reportConfig(String fileName, String sheetName, List<T> methods, List<String> headers);
}
