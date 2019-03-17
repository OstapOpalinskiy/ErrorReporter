package com.opalynskyi.errorreporter.database;

class DbConstants {
    static final String DB_NAME = "reports_db";
    static final String TABLE_NAME = "reports";
    static final int DB_VERSION = 1;

    static final String COLUMN_ID = "id";
    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_MESSAGE = "message";
    static final String COLUMN_TRACE = "trace";
    static final String COLUMN_IS_FATAL = "isFatal";
    static final String COLUMN_THREAD_NAME = "threadName";
    static final String COLUMN_IS_NEW = "isNew";

    static final int COLUMN_ID_INDEX = 0;
    static final int COLUMN_TIMESTAMP_INDEX = 1;
    static final int COLUMN_MESSAGE_INDEX = 2;
    static final int COLUMN_TRACE_INDEX = 3;
    static final int COLUMN_IS_FATAL_INDEX = 4;
    static final int COLUMN_THREAD_NAME_INDEX = 5;
    static final int COLUMN_IS_NEW_INDEX = 6;
}
