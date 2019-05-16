package com.unison.appartment.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public final static Locale STANDARD_LOCALE = Locale.US;

    public static String formatDateWithStandardLocale(Date date) {
        return formatDateWithSpecificLocale(date, STANDARD_LOCALE);
    }

    public static String formatDateWithCurrentDefaultLocale(Date date) {
        return formatDateWithSpecificLocale(date, Locale.getDefault());
    }

    public static String formatDateWithSpecificLocale(Date date, Locale locale) {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return dateFormatter.format(date);
    }

    public static Date parseDateWithStandardLocale(String dateString) throws ParseException {
        return parseDateWithSpecificLocale(dateString, STANDARD_LOCALE);
    }

    public static Date parseDateWithCurrentDefaultLocale(String dateString) throws ParseException {
        return parseDateWithSpecificLocale(dateString, Locale.getDefault());
    }

    public static Date parseDateWithSpecificLocale(String dateString, Locale locale) throws ParseException {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return dateFormatter.parse(dateString);
    }

}
