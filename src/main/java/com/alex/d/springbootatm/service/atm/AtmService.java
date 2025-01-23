package com.alex.d.springbootatm.service.atm;

import com.alex.d.springbootatm.model.Atm;
import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.model.response.*;

import java.math.BigDecimal;

public interface AtmService {

    TransactionResponse processCardTransaction(String senderCard, String recipientCard, BigDecimal amount);

    BalanceResponse checkBalanceByCardNumber(String cardNumber);

    Atm returnAtmName();

//    CardResponse processWithdrawalOrDeposit(String cardNumber, BigDecimal amount, boolean isDeposit);

    //
//            transactionRepository.save(processCardTransaction());
//
//            log.info("{} of {} for card {} was successful. Balance: {}",
//                    isDeposit ? "Deposit" : "Withdrawal", amount, cardNumber, cardBalance);
//
//            kafkaProducerService.setKafkaProducerServiceMessage(
//                    BankCardDto.builder()
//                            .cardNumber(card.getCardNumber())
//                            .balance(cardBalance)
//                            .pin("***")
//                            .build(),
//                    KafkaTopic.ATM_TOPIC.getTopicName()
//            );
//
//            if (!isDeposit) {
//
//                return WithdrawResponse.builder()
//                        .cardNumber(cardNumber)
//                        .withdrawAmount(amount.toPlainString())
//                        .build();
//            } else {
//
//                return DepositeResponse.builder()
//                        .cardNumber(cardNumber)
//                        .depositAmount(amount.toPlainString())
//                        .build();
//            }
//        } else {
//            log.warn("Attempted {} for non-existing card: {}", isDeposit ? "deposit" : "withdrawal", cardNumber);
//            throw new CardNotFoundException("Card not found with number: " + cardNumber);
//        }
//    }

    CardResponse processAtmTransaction(String cardNumber, BigDecimal amount, String transactionType);

    DepositeResponse processDeposit(String cardNumber, BigDecimal amount);

    WithdrawResponse processWithdrawal(String cardNumber, BigDecimal amount);

    BankCard fetchCardFromDb(String card);

    BigDecimal addAmountToBankCardBalance(String card, BigDecimal amount);

    BigDecimal subtractAmountFromBankCardBalance(String card, BigDecimal amount);

    BigDecimal addOrSubtractAmountFromBalance(String cardNumber, BigDecimal amount, boolean addAmount);

}
