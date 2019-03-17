package com.opalynskyi.errorreporter.async_job;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Wrapper on top of AsyncTask that simplifies usage of lambdas
 */

public class AsyncJob<T> {
    private static Executor executor = Executors.newSingleThreadExecutor();
    private Exception exception;
    private Action<T> backgroundAction;
    private SuccessAction<T> successAction;
    private ErrorAction errorAction;
    private FinalAction finalAction;
    private IsSuccess<T> isSuccessAction;

    public void cancel(boolean mayInterruptIfRunning) {
        backgroundAction = null;
        successAction = null;
        errorAction = null;
        finalAction = null;
        isSuccessAction = null;
        asyncTask.cancel(mayInterruptIfRunning);
    }

    public AsyncJob(@NonNull Action<T> backgroundAction) {
        this.backgroundAction = backgroundAction;
    }

    public void execute() {
        asyncTask.executeOnExecutor(executor);
    }

    public void executeOnExecutor(Executor executor) {
        asyncTask.executeOnExecutor(executor);
    }

    public AsyncJob<T> onSuccess(SuccessAction<T> successAction) {
        this.successAction = successAction;
        return this;
    }

    public AsyncJob<T> onError(ErrorAction errorAction) {
        this.errorAction = errorAction;
        return this;
    }

    public AsyncJob<T> onFinally(FinalAction finalAction) {
        this.finalAction = finalAction;
        return this;
    }

    public AsyncJob<T> isSuccess(IsSuccess<T> isSuccessAction) {
        this.isSuccessAction = isSuccessAction;
        return this;
    }

    private boolean success(Exception e, T result) {
        if (isSuccessAction == null) {
            return e == null && result != null;
        } else {
            return isSuccessAction.isSuccess(e, result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private final AsyncTask<Void, Void, T> asyncTask = new AsyncTask<Void, Void, T>() {

        @Override
        protected T doInBackground(Void... voids) {
            T result = null;
            try {
                if (!isCancelled() && backgroundAction != null) {
                    result = backgroundAction.perform();
                }
            } catch (Exception e) {
                exception = e;
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(T t) {
            if (successAction != null && success(exception, t)) {
                successAction.onSuccess(t);
            }
            if (errorAction != null && !success(exception, t)) {
                errorAction.onError(exception);
            }
            if (finalAction != null) {
                finalAction.onDone();
            }
        }
    };
}
