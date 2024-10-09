package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.TransactionDetailsDTO;
import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.BankCardRepository;
import com.alex.d.springbootatm.response.ErrorResponse;
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

    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private TransactionServiceImpl transactionService;


    public ResponseEntity generateClientReport() {
        String fileName = "report.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");

            Row createHeaderRow = sheet.createRow(0);
            createHeaderRow.createCell(0).setCellValue("id");
            createHeaderRow.createCell(1).setCellValue("card_number");
            createHeaderRow.createCell(2).setCellValue("pin_number");
            createHeaderRow.createCell(3).setCellValue("balance");

            List<BankCardModel> cards = bankCardRepository.findAll();

            int rowNum = 1;
            for (BankCardModel card : cards) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(card.getId());
                row.createCell(1).setCellValue(card.getCardNumber());
                row.createCell(2).setCellValue(card.getPinNumber());
                row.createCell(3).setCellValue(String.valueOf(card.getBalance()));
            }

            Row getHeaderRow = sheet.getRow(0);
            if (getHeaderRow != null) {
                for (int i = 0; i < createHeaderRow.getPhysicalNumberOfCells(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

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

            Row createHeaderRow = sheet.createRow(0);
            createHeaderRow.createCell(0).setCellValue("card_number");
            createHeaderRow.createCell(1).setCellValue("amount");
            createHeaderRow.createCell(2).setCellValue("transaction_type");
            createHeaderRow.createCell(3).setCellValue("sender");
            createHeaderRow.createCell(4).setCellValue("recipient");
            createHeaderRow.createCell(5).setCellValue("balance");
            createHeaderRow.createCell(6).setCellValue("time_stamp");

            List<TransactionDetailsDTO> transactionDetails = transactionService.getTransactionDetailsByCardNumber(cardNumber);
            if (!transactionDetails.isEmpty()) {
                int rowNum = 1;
                for (TransactionDetailsDTO transaction : transactionDetails) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(transaction.getCardNumber());
                    row.createCell(1).setCellValue(String.valueOf(transaction.getAmount()));
                    row.createCell(2).setCellValue(transaction.getTransactionType());
                    row.createCell(3).setCellValue(transaction.getSender());
                    row.createCell(4).setCellValue(transaction.getRecipient());
                    row.createCell(5).setCellValue(transaction.getBalance().toString());
                    row.createCell(6).setCellValue(transaction.getTimestamp().toString());
                }
            }

            Row getHeaderRow = sheet.getRow(0);
            if (getHeaderRow != null) {
                for (int i = 0; i < createHeaderRow.getPhysicalNumberOfCells(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return report(fileName);
    }

    public ResponseEntity report(String fileName) {
        File file = new File(fileName);
        ErrorResponse error = new ErrorResponse();
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

}
