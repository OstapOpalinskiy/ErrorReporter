package com.opalynskyi.crashreportertestapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.opalynskyi.errorreporter.Report;

public class NotificationHelper {
    private static final String CHANNEL_ID = "reports_channel_id";
    private static int notificationId = 0;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showReportNotification(Context context, Report report) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, createNotification(context, report));
    }

    private static Notification createNotification(Context context, Report report) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_error)
                .setContentTitle(context.getString(R.string.notification_title) + report.getMessage())
                .setContentText(context.getString(R.string.tap_to_send_via_email))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(context, 0, sendReportViaEmailIntent(context, report), 0))
                .setChannelId(CHANNEL_ID);

        if (report.isFatal()) {
            builder.setColor(Color.RED);
        }

        return builder.build();
    }

    private static Intent sendReportViaEmailIntent(Context context, Report report) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        String subject = getEmailSubject(context, report);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Utils.createEmailReport(context, report));
        return emailIntent;
    }

    private static String getEmailSubject(Context context, Report report) {
        String fatalErrorLabel = context.getString(R.string.fatal_error_reported);
        String nonFatalErrorLabel = context.getString(R.string.non_fatal_error_reported);
        return report.isFatal() ? fatalErrorLabel : nonFatalErrorLabel;
    }
}
