package com.alex.d.springbootatm.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeService {

    private final String DATE_FORMAT = "dd.MM.yyyy | HH:mm:ss";
    public String getFormatedDateTime(String inputDate, String outputFormat) {
        LocalDateTime date = LocalDateTime.parse(inputDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat);
        return date.format(formatter);
    }
}
