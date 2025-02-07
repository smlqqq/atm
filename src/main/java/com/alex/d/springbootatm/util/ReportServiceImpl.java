package com.alex.d.springbootatm.util;

import com.alex.d.springbootatm.dto.BankCardDto;
import com.alex.d.springbootatm.dto.BankCardTransactionDto;
import com.alex.d.springbootatm.service.atm.BankCardTransactionDetailsServiceImpl;
import com.alex.d.springbootatm.service.card.BankCardService;
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
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final BankCardTransactionDetailsServiceImpl transactionService;
    private final BankCardService bankCardService;

    public ReportServiceImpl(BankCardTransactionDetailsServiceImpl transactionService, BankCardService bankCardService) {
        this.transactionService = transactionService;
        this.bankCardService = bankCardService;
    }


    public ResponseEntity<?> generateReport() {

        List<BankCardDto> cardsDetails = bankCardService.fetchAllBankCardsData();
        String[] headers = {"Card", "Hashed Pin", "Balance"};

        return reportConfig("report.xls", "clients", cardsDetails, headers);
    }

    public ResponseEntity<?> generateReportByCardNumber(String cardNumber) {

        List<BankCardTransactionDto> cardTransactionDetails = transactionService.getTransactionDetailsByCardNumber(cardNumber);

        String fileName = cardNumber + "_personal_card_report.xlsx";
        String[] headers = {"Sender Card Number",
                "Sender Balance",
                "Transaction Type",
                "ATM Name",
                "Recipient Card Number",
                "Amount",
                "Recipient Balance",
                "Timestamp"};

        return reportConfig(fileName, "personal", cardTransactionDetails, headers);
    }


    public ResponseEntity<?> createReport(String fileName) {
        File file = new File(fileName);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    public void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void addHeaders(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    public <T> ResponseEntity<?> reportConfig(String fileName, String sheetName, List<T> methods, String[] headers) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetName);

            addHeaders(sheet, headers);

            int rowNum = 1;

            Class<?> dtoClass = methods.get(0).getClass();

            Field[] fields = dtoClass.getDeclaredFields();

            //Fill data
            for (T dto : methods) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    try {
                        Object value = fields[i].get(dto);
                        row.createCell(i).setCellValue(value != null ? value.toString() : "");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            autoSizeColumns(sheet, headers.length);

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return createReport(fileName);
    }

}
