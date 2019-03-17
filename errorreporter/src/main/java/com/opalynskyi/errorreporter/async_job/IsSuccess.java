package com.opalynskyi.errorreporter.async_job;

/**
 * Created on 14.12.2017.
 */

public interface IsSuccess<T> {
    boolean isSuccess(Exception e, T t);
}
