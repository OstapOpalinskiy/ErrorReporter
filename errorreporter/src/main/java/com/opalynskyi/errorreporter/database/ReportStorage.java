package com.opalynskyi.errorreporter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.opalynskyi.errorreporter.Report;
import com.opalynskyi.errorreporter.async_job.AsyncJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents persistent storage for error reports
 */
public class ReportStorage {
    private static final String TAG = ReportStorage.class.getSimpleName();
    private static final String[] REPORT_COLUMNS = {
            DbConstants.COLUMN_ID,
            DbConstants.COLUMN_TIMESTAMP,
            DbConstants.COLUMN_MESSAGE,
            DbConstants.COLUMN_TRACE,
            DbConstants.COLUMN_IS_FATAL,
            DbConstants.COLUMN_THREAD_NAME,
            DbConstants.COLUMN_IS_NEW
    };

    private static ReportStorage instance;
    private SQLiteDatabase database;


    private ReportStorage(Context context) {
        database = new DbHelper(context).getWritableDatabase();
    }

    /**
     * Returns instance of ReportStorage
     */
    public static ReportStorage getInstance(Context context) {
        if (instance == null) {
            synchronized (ReportStorage.class) {
                if (instance == null) {
                    instance = new ReportStorage(context);
                }
            }
        }
        return instance;
    }

    public AsyncJob<Boolean> deleteReportsOlderThan(int days) {
        return new AsyncJob<>(() -> {
            String date = "date('now','-" + days + "day')";
            String query = "DELETE FROM " + DbConstants.TABLE_NAME + " WHERE " + DbConstants.COLUMN_TIMESTAMP + " <= " + date;
            Log.d(TAG, query);
            database.execSQL(query);
            return true;
        });
    }

    /**
     * Saves report in database
     *
     * @param report
     */
    public long saveReportSync(Report report) throws SQLiteException {
        ContentValues contentValues = getContentValues(report);
        Log.d(TAG, "Save report:" + contentValues.get(DbConstants.COLUMN_THREAD_NAME));
        return database.insert(DbConstants.TABLE_NAME, null, contentValues);
    }

    public AsyncJob<Long> saveReport(Report report) throws SQLiteException {
        return new AsyncJob<>(() -> saveReportSync(report));
    }


    /**
     * Sets isNew = false for the report and updates record in database
     *
     * @param report
     */
    public AsyncJob<Integer> markAsShown(Report report) throws SQLiteException {
        return new AsyncJob<>(() -> {
            report.setNew(false);
            ContentValues contentValues = getContentValues(report);
            return database.update(DbConstants.TABLE_NAME, contentValues, DbConstants.COLUMN_ID + " = ?",
                    new String[]{Long.toString(report.getId())});
        });
    }

    /**
     * Returns list of reports for which isNew == false
     */
    public AsyncJob<List<Report>> getNewReports() {
        return new AsyncJob<>(() -> {
            List<Report> list = new ArrayList<>();
            try (Cursor cursor = database.query(DbConstants.TABLE_NAME, REPORT_COLUMNS, DbConstants.COLUMN_IS_NEW + " = ?",
                    new String[]{Long.toString(1)}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        Report report = readReport(cursor);
                        list.add(report);
                    } while (cursor.moveToNext());
                }
            }
            return list;
        });
    }

    private ContentValues getContentValues(Report report) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.COLUMN_TIMESTAMP, report.getTimestamp());
        contentValues.put(DbConstants.COLUMN_MESSAGE, report.getMessage());
        contentValues.put(DbConstants.COLUMN_TRACE, report.getTrace());
        contentValues.put(DbConstants.COLUMN_IS_FATAL, report.isFatal());
        contentValues.put(DbConstants.COLUMN_THREAD_NAME, report.getThreadName());
        contentValues.put(DbConstants.COLUMN_IS_NEW, report.isNew());
        return contentValues;
    }

    private Report readReport(Cursor cursor) {
        Report report = new Report();
        report.setId(cursor.getLong(DbConstants.COLUMN_ID_INDEX));
        report.setTimestamp(cursor.getLong(DbConstants.COLUMN_TIMESTAMP_INDEX));
        report.setMessage(cursor.getString(DbConstants.COLUMN_MESSAGE_INDEX));
        report.setTrace(cursor.getString(DbConstants.COLUMN_TRACE_INDEX));
        report.setFatal(cursor.getInt(DbConstants.COLUMN_IS_FATAL_INDEX) == 1);
        report.setThreadName(cursor.getString(DbConstants.COLUMN_THREAD_NAME_INDEX));
        report.setNew(cursor.getInt(DbConstants.COLUMN_IS_NEW_INDEX) == 1);
        return report;
    }

    public AsyncJob<List<Report>> getAll() {
        return new AsyncJob<List<Report>>(this::getReportsSync);
    }

    public List<Report> getReportsSync() {
        List<Report> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DbConstants.TABLE_NAME;
        try (Cursor cursor = database.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Report report = readReport(cursor);
                    list.add(report);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }
}
