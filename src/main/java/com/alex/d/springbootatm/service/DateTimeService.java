package com.alex.d.springbootatm.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeService {

    public String getFormatedDateTime(String inputDate, String outputFormat) {
        LocalDateTime date = LocalDateTime.parse(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat);
        return date.format(formatter);
    }
}
