package com.red.api.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RedThreadExecutor extends ThreadPoolExecutor {
    public static final int DEFAULT_MIN_THREADS = 20;
    public static final int DEFAULT_MAX_THREADS = 200;
    public static final int DEFAULT_MAX_IDLE_TIME = 60 * 1000; // 1 minutes

    protected AtomicInteger submittedTasksCount;	// 正在处理的任务数
    private int maxSubmittedTaskCount;				// 最大允许同时处理的任务数

    public RedThreadExecutor() {
        this(DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
    }

    public RedThreadExecutor(int coreThread, int maxThreads) {
        this(coreThread, maxThreads, maxThreads);
    }

    public RedThreadExecutor(int coreThread, int maxThreads, long keepAliveTime, TimeUnit unit) {
        this(coreThread, maxThreads, keepAliveTime, unit, maxThreads);
    }

    public RedThreadExecutor(int coreThreads, int maxThreads, int queueCapacity) {
        this(coreThreads, maxThreads, queueCapacity, Executors.defaultThreadFactory());
    }

    public RedThreadExecutor(int coreThreads, int maxThreads, int queueCapacity, ThreadFactory threadFactory) {
        this(coreThreads, maxThreads, DEFAULT_MAX_IDLE_TIME, TimeUnit.MILLISECONDS, queueCapacity, threadFactory);
    }

    public RedThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit, int queueCapacity) {
        this(coreThreads, maxThreads, keepAliveTime, unit, queueCapacity, Executors.defaultThreadFactory());
    }

    public RedThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit,
                             int queueCapacity, ThreadFactory threadFactory) {
        this(coreThreads, maxThreads, keepAliveTime, unit, queueCapacity, threadFactory, new AbortPolicy());
    }

    public RedThreadExecutor(int coreThreads, int maxThreads, long keepAliveTime, TimeUnit unit,
                             int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(coreThreads, maxThreads, keepAliveTime, unit, new RedQueue(), threadFactory, handler);
        ((RedQueue) getQueue()).setIOThreadExecutor(this);

        submittedTasksCount = new AtomicInteger(0);

        // 最大并发任务限制： 队列buffer数 + 最大线程数
        maxSubmittedTaskCount = queueCapacity + maxThreads;
    }

    @Override
    public void execute(Runnable command) {
        int count = submittedTasksCount.incrementAndGet();

        if (count > maxSubmittedTaskCount) {
            submittedTasksCount.decrementAndGet();
            getRejectedExecutionHandler().rejectedExecution(command, this);
        }

        try {
            super.execute(command);
        }catch (RejectedExecutionException rx){
            submittedTasksCount.decrementAndGet();
            getRejectedExecutionHandler().rejectedExecution(command, this);
        }
    }

    public int getSubmittedTasksCount() {
        return this.submittedTasksCount.get();
    }

    public int getMaxSubmittedTaskCount() {
        return maxSubmittedTaskCount;
    }

    protected void afterExecute(Runnable r, Throwable t) {
        submittedTasksCount.decrementAndGet();
    }
}

class RedQueue extends LinkedTransferQueue<Runnable>{
    RedThreadExecutor threadPoolExecutor;

    public void setIOThreadExecutor(RedThreadExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public boolean offer(Runnable runnable) {

        int poolSize = threadPoolExecutor.getPoolSize();

        if(poolSize == threadPoolExecutor.getMaximumPoolSize()){
            return super.offer(runnable);
        }

//        if(threadPoolExecutor.getSubmittedTasksCount() <= poolSize){
//            return super.offer(runnable);
//        }
        if(poolSize < threadPoolExecutor.getMaxSubmittedTaskCount()){
            return false;
        }

        return super.offer(runnable);
    }
}
