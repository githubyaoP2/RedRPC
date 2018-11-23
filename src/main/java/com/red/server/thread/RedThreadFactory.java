package com.red.server.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RedThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger threadNum = new AtomicInteger(1);

    public RedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r,name+threadNum);
        threadNum.incrementAndGet();
        return thread;
    }
}
