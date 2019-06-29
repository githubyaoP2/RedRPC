package com.red.api.message;

import java.util.concurrent.*;

public class ResponseFuture implements Future {

    Object value;
    CountDownLatch countDownLatch = new CountDownLatch(1);
    private final int RUNNING = 0;
    private final int CANCELLED = 1;
    private final int FINISHED = 2;
    volatile int state = RUNNING;//0 正在运行、1 被取消、2 处理完

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
        return state == CANCELLED;
    }

    @Override
    public boolean isDone() {
        return state == FINISHED;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return value;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        countDownLatch.await(timeout,unit);
        if(value != null){
            return value;
        }
        return null;
    }
}
