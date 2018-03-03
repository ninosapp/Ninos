package com.ninos.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Window;
import android.widget.DatePicker;

import com.ninos.listeners.DateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.ninos.utils.CrashUtil.report;

/**
 * Created by FAMILY on 14-12-2017.
 */

public class DateUtil {
    public static final String FULL_DATE = "MMM dd, yyyy";
    public static final String yyyyMMdd = "yyyyMMdd";

    public void datePicker(final Context context, final DateSetListener dateSetListener, final Long dateTime) {

        try {
            final Calendar c = Calendar.getInstance();

            if (dateTime != null) {
                Date localDate = new Date(dateTime);
                c.setTime(localDate);
            }

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            final int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            if (view.isShown()) {

                                monthOfYear = monthOfYear + 1;
                                String month = "" + monthOfYear;
                                String date = "" + dayOfMonth;

                                if (monthOfYear < 10) {
                                    month = "0" + monthOfYear;
                                }

                                if (dayOfMonth < 10) {
                                    date = "0" + dayOfMonth;
                                }

                                String fullDate = year + month + date;

                                String parsedDate = convertDateStringFormat(fullDate, yyyyMMdd, FULL_DATE);

                                if (dateSetListener != null) {
                                    dateSetListener.onDateSet(parsedDate);
                                }
                            }
                        }
                    }, year, month, day);

            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.show();
        } catch (Exception e) {
            report(e);
        }
    }

    public String convertDateStringFormat(String dateValue, String fromFormat, String toFormat) {

        String dateValueNewFormat = null;

        try {

            Date date = getSimpleDateFormat(fromFormat).parse(dateValue);

            dateValueNewFormat = formatDateToString(date, toFormat);

        } catch (Exception e) {
            report(e);
        }

        return dateValueNewFormat;
    }

    private SimpleDateFormat getSimpleDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.US);
    }

    public String formatDateToString(Date date, String format) {

        String formattedDate = null;

        try {
            formattedDate = getSimpleDateFormat(format).format(date);
        } catch (Exception e) {
            report(e);
        }

        return formattedDate;
    }

    public Date formatStringToDate(String dateValue, String format) {

        Date date = null;

        try {
            date = getSimpleDateFormat(format).parse(dateValue);
        } catch (Exception e) {
            report(e);
        }

        return date;
    }

    public Date getDateWithoutTimeStamp(Date date) {

        Date dateWithoutTimeStamp = null;

        try {

            if (date != null) {

                Calendar cal = Calendar.getInstance();

                cal.setTime(date);

                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                dateWithoutTimeStamp = cal.getTime();
            }

        } catch (Exception e) {
            report(e);
        }

        return dateWithoutTimeStamp;
    }
}
