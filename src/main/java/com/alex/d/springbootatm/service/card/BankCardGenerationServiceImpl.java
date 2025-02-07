package com.alex.d.springbootatm.service.card;

import com.alex.d.springbootatm.model.BankCard;
import com.alex.d.springbootatm.service.Security.SecurityServiceAtm;
import com.alex.d.springbootatm.util.LuhnsAlgorithm;
import org.springdoc.core.service.SecurityService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class BankCardGenerationServiceImpl implements BankCardGenerationService {

    private final SecurityServiceAtm securityService;

    public BankCardGenerationServiceImpl(SecurityServiceAtm securityService) {
        this.securityService = securityService;
    }

    @Override
    public BigDecimal generateBalance() {
        return BigDecimal.valueOf(0);
    }

//    @Override
//    public String generatePinCode() {
//        Random random = new Random();
//        return String.format("%04d", random.nextInt(10000));
//    }

    @Override
    public char[] pinCodeGenerator() {
        Random random = new Random();
        char[] password = new char[4];
        for (int i = 0; i < 4; i++) {
            password[i] = (char) ('0' + random.nextInt(10));
        }
        return password;
    }

    @Override
    public String bankCardNumGenerator() {
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
    public BankCard buildCardModel(String pin) {
        return BankCard.builder()
                .cardNumber(bankCardNumGenerator())
                .pinNumber(securityService.passwordEncode(pin))
                .balance(generateBalance())
                .build();
    }
}
