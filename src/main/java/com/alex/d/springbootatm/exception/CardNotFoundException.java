package com.alex.d.springbootatm.exception;

public class CardNotFoundException extends Throwable{
    public CardNotFoundException(String message) {
        super(message);
    }
}
