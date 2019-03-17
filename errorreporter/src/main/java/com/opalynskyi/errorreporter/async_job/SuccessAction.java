package com.opalynskyi.errorreporter.async_job;

/**
 * Created on 13.12.2017.
 */

public interface SuccessAction<T> {
    void onSuccess(T result);
}
