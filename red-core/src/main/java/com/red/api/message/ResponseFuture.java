package com.red.api.message;

import java.util.concurrent.*;

public class ResponseFuture implements Future {

    Object value;
    CountDownLatch countDownLatch = new CountDownLatch(1);

    public void setValue(Object value) {
        this.value = value;
    }

    public void countDown(){
        countDownLatch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return value;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
