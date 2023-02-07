package com.codenomads.springrestapidemo.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateParser {

    public Date parse(String dateToParse) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(dateToParse);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
