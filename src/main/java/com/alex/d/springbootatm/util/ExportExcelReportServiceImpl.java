package com.alex.d.springbootatm.util;

import com.alex.d.springbootatm.dto.BankCardDto;
import com.alex.d.springbootatm.dto.BankCardTransactionDto;
import com.alex.d.springbootatm.service.atm.BankCardTransactionDetailsServiceImpl;
import com.alex.d.springbootatm.service.card.BankCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class ExportExcelReportServiceImpl implements ExportExcelReportService {

    private final BankCardTransactionDetailsServiceImpl transactionService;
    private final BankCardService bankCardService;
    private final ReportService reportService;

    public ExportExcelReportServiceImpl(BankCardTransactionDetailsServiceImpl transactionService, BankCardService bankCardService, ReportService reportService) {
        this.transactionService = transactionService;
        this.bankCardService = bankCardService;
        this.reportService = reportService;
    }

    @Override
    public ResponseEntity<?> exportCardsDataToExcel() {
        List<BankCardDto> bankCardDtoDetails = bankCardService.fetchAllBankCardsData();
        return reportService.reportConfig("report.xls", "clients", bankCardDtoDetails, reportService.createHeaders("Card", "Balance"));
    }

    @Override
    public ResponseEntity<?> exportCardTransactionsHistoryToExcel(String cardNumber) {
        List<BankCardTransactionDto> cardTransactionDetails = transactionService.getTransactionDetailsByCardNumber(cardNumber);
        return reportService.reportConfig(MessageFormat.format("{0}_personal_card_report.xlsx", cardNumber), "personal", cardTransactionDetails,
        reportService.createHeaders(
                        "Sender Card Number",
                        "Sender Balance",
                        "Transaction Type",
                        "ATM Name",
                        "Recipient Card Number",
                        "Amount",
                        "Recipient Balance",
                        "Timestamp")
        );
    }
}
