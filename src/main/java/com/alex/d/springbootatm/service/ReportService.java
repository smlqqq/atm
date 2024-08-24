package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.model.BankCardModel;
import com.alex.d.springbootatm.repository.BankCardRepository;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private BankCardRepository bankCardRepository;

    public void generateClientReport() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("card_number");
            headerRow.createCell(2).setCellValue("pin_number");
            headerRow.createCell(3).setCellValue("balance");

            List<BankCardModel> cards = bankCardRepository.findAll();

            int rowNum = 1;
            for (BankCardModel card : cards) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(card.getId());
                row.createCell(1).setCellValue(card.getCardNumber());
                row.createCell(2).setCellValue(card.getPinNumber());
                row.createCell(3).setCellValue(String.valueOf(card.getBalance()));
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream("report.xlsx")) {
                workbook.write(outputStream);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
