package com.alex.d.springbootatm.util;

import com.alex.d.springbootatm.model.dto.CardDto;
import com.alex.d.springbootatm.model.dto.TransactionDto;
import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.service.atm.TransactionDetailsServiceImpl;
import com.alex.d.springbootatm.service.card.CardService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    private final String DATE_FORMAT = "dd.MM.yyyy | HH:mm:ss";

    @Autowired
    private TransactionDetailsServiceImpl transactionService;

    @Autowired
    private DateTimeService dateTimeService;

    @Autowired
    private CardService cardService;

    public ResponseEntity generateClientReport() {
        String fileName = "Report.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");

            addHeaders(sheet, "Card", "Pin", "Balance");

            List<CardDto> cards = cardService.getAllCards();

            int rowNum = 1;
            for (CardDto card : cards) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(card.getCardNumber());
                row.createCell(1).setCellValue(card.getPin());
                row.createCell(2).setCellValue(String.valueOf(card.getBalance()));
            }

           autoSizeColumns(sheet,3);

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return report(fileName);
    }

    public ResponseEntity generateIndividualClientReport(String cardNumber) {
        String fileName = cardNumber + "_personal_card_report.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Personal Data");

            addHeaders(sheet,
                    "Sender Card Number",
                    "Sender Balance",
                    "Transaction Type",
                    "ATM Name",
                    "Recipient Card Number",
                    "Amount",
                    "Recipient Balance",
                    "Timestamp"
            );


            List<TransactionDto> transactionDetails = transactionService.getTransactionDetailsByCardNumber(cardNumber);
            if (!transactionDetails.isEmpty()) {
                int rowNum = 1;
                for (TransactionDto transaction : transactionDetails) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(transaction.getSender());
                    row.createCell(1).setCellValue(String.valueOf(transaction.getSenderBalance()));
                    row.createCell(2).setCellValue(transaction.getTransactionType());
                    row.createCell(3).setCellValue(transaction.getAtmName());
                    row.createCell(4).setCellValue(transaction.getRecipient());
                    row.createCell(5).setCellValue(String.valueOf(transaction.getAmount()));
                    row.createCell(6).setCellValue(String.valueOf(transaction.getRecipientBalance()));
                    row.createCell(7).setCellValue(dateTimeService.getFormatedDateTime(String.valueOf(transaction.getTimestamp()), DATE_FORMAT));
                }
            }

            autoSizeColumns(sheet, 8);

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return report(fileName);
    }

    private ResponseEntity report(String fileName) {
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

    private void addHeaders(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

}
