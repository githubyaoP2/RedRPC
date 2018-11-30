package com.red.model;

import com.red.message.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MetricModel implements Message {
   private List<MetricServiceModel> metricServiceModelList;

    public List<MetricServiceModel> getMetricServiceModelList() {
        return metricServiceModelList;
    }

    public void setMetricServiceModelList(List<MetricServiceModel> metricServiceModelList) {
        this.metricServiceModelList = metricServiceModelList;
    }
}
