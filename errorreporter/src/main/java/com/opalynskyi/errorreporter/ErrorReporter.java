package com.opalynskyi.errorreporter;

import android.content.Context;
import android.util.Log;

import com.opalynskyi.errorreporter.database.ReportStorage;

/**
 * Class that allows to persist crash reports and notify user about errors
 */
public final class ErrorReporter {
    private static final String TAG = ErrorReporter.class.getSimpleName();
    private static final int DAYS_TO_KEEP_REPORTS = 7;
    private boolean isInitialised = false;
    private ReportStorage reportStorage;
    private OnNewReportListener listener;
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private static ErrorReporter instance;

    private ErrorReporter() {
    }

    /**
     * Returns instance of CrashReporter
     */
    public static ErrorReporter getInstance() {
        if (instance == null) {
            synchronized (ReportStorage.class) {
                if (instance == null) {
                    instance = new ErrorReporter();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes database for reports, deletes obsolete
     * reports and sets handler to catch all unhandled errors.
     * This method should be called once.
     *
     * @param context Needed to initialise database.
     */
    public void initialize(Context context) {
        if (isInitialised) {
            throw new RuntimeException("CrashReporter should be initialized only once");
        }
        reportStorage = ReportStorage.getInstance(context);
        reportStorage.deleteReportsOlderThan(DAYS_TO_KEEP_REPORTS)
                .execute();
        registerGlobalExceptionHandler();
        isInitialised = true;
    }

    /**
     * Sets listener that will be triggered first time after setting if new reports are available
     * and each time after {@link ErrorReporter#report(Thread, Throwable)} method was called
     */
    public void setReportListener(OnNewReportListener reportListener) {
        listener = reportListener;
        notifyListenerWithAvailableReports();
    }

    /**
     * Removes OnNewReportListener to avoid memory leaks
     */
    public void removeReportListener() {
        listener = null;
    }

    /**
     * Reports error to the listener. Calling this, method does not saves report
     * to the persistent storage and it will not be available after closing the app.
     */
    public void report(Thread thread, Throwable throwable) {
        Report report = createReport(thread, throwable, false);
        report.setNew(false);
        reportStorage.saveReport(report)
                .onError((e) -> Log.e(TAG, "Error, saving report with id: " + report.getId()))
                .execute();
        listener.onNewReport(report);
    }

    private void notifyListenerWithAvailableReports() {
        reportStorage.getNewReports()
                .onSuccess((savedReports) -> {
                    for (Report report : savedReports) {
                        listener.onNewReport(report);
                        markReportAsShown(report);
                    }
                })
                .onError((e) -> Log.e(TAG, "Error, reading reports from database"))
                .execute();
    }

    private void markReportAsShown(Report report) {
        reportStorage.markAsShown(report)
                .isSuccess((e, result) -> e == null && result != -1)
                .onError((e) -> Log.e(TAG, "Error, updating report with id: " + report.getId()))
                .execute();
    }

    private void registerGlobalExceptionHandler() {
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            Report report = createReport(paramThread, paramThrowable, true);
            reportStorage.saveReportSync(report);
            exceptionHandler.uncaughtException(paramThread, paramThrowable);
        });
    }

    private Report createReport(Thread thread, Throwable throwable, boolean isFatal) {
        Report report = new Report();
        report.setTimestamp(System.currentTimeMillis());
        report.setMessage(throwable.getMessage());
        report.setTrace(Log.getStackTraceString(throwable));
        report.setFatal(isFatal);
        report.setThreadName(thread.getName());
        return report;
    }

    public interface OnNewReportListener {
        void onNewReport(Report report);
    }
}
