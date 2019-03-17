package com.opalynskyi.crashreportertestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.opalynskyi.errorreporter.ErrorReporter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View btnCrash = findViewById(R.id.btnCrash);
        View btnCaughtCrash = findViewById(R.id.btnCaughtCrash);
        btnCrash.setOnClickListener(v -> generateFatalException());
        btnCaughtCrash.setOnClickListener(v -> generateCaughtException());

        ErrorReporter.getInstance().setReportListener(report -> NotificationHelper.showReportNotification(MainActivity.this, report));

        NotificationHelper.createNotificationChannel(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ErrorReporter.getInstance().removeReportListener();
    }

    private void generateFatalException() {
        throw new RuntimeException("Test exception");
    }

    private void generateCaughtException() {
        try {
            generateFatalException();
        } catch (Exception e) {
            ErrorReporter.getInstance().report(Thread.currentThread(), e);
            Log.e(TAG, "Caught exception: " + e.getMessage());

        }
    }
}
