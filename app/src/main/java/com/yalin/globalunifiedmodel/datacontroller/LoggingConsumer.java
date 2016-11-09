package com.yalin.globalunifiedmodel.datacontroller;

import com.yalin.datacontroller.FailureListener;
import com.yalin.datacontroller.MaybeConsumer;
import com.yalin.globalunifiedmodel.log.StatLog;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public abstract class LoggingConsumer<T> implements MaybeConsumer<T> {
    public static <T> LoggingConsumer<T> expectSuccess(String tag, String operation) {
        return new LoggingConsumer<T>(tag, operation) {
            @Override
            public void success(T value) {
                // do nothing
            }
        };
    }

    public static FailureListener logFailure(final String operation) {
        return new FailureListener() {
            private static final String TAG = "logFailure";

            @Override
            public void fail(Exception e) {
                StatLog.printLog(TAG, "Failed: " + operation, e);
            }
        };
    }

    private final String mTag;
    private final String mOperation;

    public LoggingConsumer(String tag, String operation) {
        mTag = tag;
        mOperation = operation;
    }

    @Override
    public void fail(Exception e) {
        StatLog.printLog(mTag, "Failed: " + mOperation, e);
    }
}
