package com.alex.d.springbootatm.exception;

import org.apache.logging.log4j.message.Message;

public class InsufficientFundsException extends Throwable {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
