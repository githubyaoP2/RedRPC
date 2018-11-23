package com.red.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InvokerModel {
    private String instanceName;
    private String methodName;
    private AtomicInteger invokeCount = new AtomicInteger(0);
    private AtomicInteger succCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);
    private AtomicInteger timeOutCount = new AtomicInteger(0);
    private AtomicLong timeOutSum = new AtomicLong(0);
    private AtomicLong timeOutMin = new AtomicLong(0);
    private AtomicLong timeOutMax = new AtomicLong(0);

    public InvokerModel(String instanceName, String methodName) {
        this.instanceName = instanceName;
        this.methodName = methodName;
    }

    public AtomicInteger getInvokeCount() {
        return invokeCount;
    }

    public AtomicInteger getSuccCount() {
        return succCount;
    }

    public AtomicInteger getFailCount() {
        return failCount;
    }

    public AtomicInteger getTimeOutCount() {
        return timeOutCount;
    }

    public AtomicLong getTimeOutSum() {
        return timeOutSum;
    }

    public void setTimeOutSum(AtomicLong timeOutSum) {
        this.timeOutSum = timeOutSum;
    }

    public AtomicLong getTimeOutMin() {
        return timeOutMin;
    }

    public void setTimeOutMin(AtomicLong timeOutMin) {
        this.timeOutMin = timeOutMin;
    }

    public AtomicLong getTimeOutMax() {
        return timeOutMax;
    }

    public void setTimeOutMax(AtomicLong timeOutMax) {
        this.timeOutMax = timeOutMax;
    }
}
