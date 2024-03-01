package com.alex.d.springbootatm.service;

import org.springframework.stereotype.Service;

@Service
public class LuhnsAlgorithm {
    protected static int calculateLuhnChecksum(String number) {
        int sum = 0;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if ((number.length() - i) % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        return (sum * 9) % 10;
    }
    public static int calculateChecksum(final String number) {
        final int checksum = 10 - calculateLuhnChecksum(number) % 10;
        return checksum % 10;
    }

    public static boolean isCorrectNumber(final String cardNumber) {
        return calculateLuhnChecksum(cardNumber) % 10 == 0;
    }
}
