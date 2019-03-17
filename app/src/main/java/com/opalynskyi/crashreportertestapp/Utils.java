package com.opalynskyi.crashreportertestapp;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;

import com.opalynskyi.crashreportertestapp.R;
import com.opalynskyi.errorreporter.Report;

import java.util.Date;

public class Utils {
    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static String getFormattedDate(long millis) {
        return DateFormat.format(DATE_FORMAT, new Date(millis)).toString();
    }

    public static String createEmailReport(Context context, Report report) {
        String formattedDate = getFormattedDate(report.getTimestamp());
        String bullet = "\u25CF ";
        return  bullet + context.getString(R.string.date) + formattedDate + "\n\n" +
                bullet + context.getString(R.string.message) + report.getMessage() + "\n\n" +
                bullet + context.getString(R.string.is_fatal_exception) + report.isFatal() + "\n\n" +
                bullet + context.getString(R.string.device_info) + getDeviceInfo() + "\n\n" +
                bullet + context.getString(R.string.trace) + report.getTrace();
    }

    private static String getDeviceInfo() {
        return Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    }
}
