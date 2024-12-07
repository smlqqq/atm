package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.CardModel;
import com.alex.d.springbootatm.util.LuhnsAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class CardGenerationServiceImpl implements CardGenerationService {

    @Override
    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

    @Override
    public String generatePinCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    @Override
    public String hashPinCode(String pinCode) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(pinCode);
    }

    @Override
    public String generateCreditCardNumber() {
        StringBuilder sb = new StringBuilder("400000");
        for (int i = 1; i < 10; i++) {
            sb.append((int) (Math.random() * 10));
        }
        String prefix = sb.toString();
        int checksum = LuhnsAlgorithm.calculateChecksum(prefix);
        sb.append(checksum);
        return sb.toString();
    }

    @Override
    public CardModel buildCardModel(String pin) {
        return CardModel.builder()
                .cardNumber(generateCreditCardNumber())
                .pinNumber(hashPinCode(pin))
                .balance(generateBalance())
                .build();
    }
}
