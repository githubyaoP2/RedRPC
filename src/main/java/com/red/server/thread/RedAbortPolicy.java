package com.red.server.thread;

import java.util.concurrent.ThreadPoolExecutor;

public class RedAbortPolicy extends ThreadPoolExecutor.AbortPolicy {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        super.rejectedExecution(r, e);
    }
}
