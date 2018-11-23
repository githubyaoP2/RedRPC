package com.red.log;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class LoggerProducer {
    Queue<String> loggerQueue = new PriorityBlockingQueue<>();
}
