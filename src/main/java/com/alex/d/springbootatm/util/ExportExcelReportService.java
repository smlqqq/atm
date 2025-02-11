package com.alex.d.springbootatm.util;

import org.springframework.http.ResponseEntity;

public interface ExportExcelReportService {

    ResponseEntity<?> exportCardsDataToExcel();

    ResponseEntity<?> exportCardTransactionsHistoryToExcel(String cardNumber);

}
