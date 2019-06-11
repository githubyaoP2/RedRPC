package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.Request;
import com.red.api.message.Response;

public interface HaStrategy {
    Response call(Request request, LoadBalance loadBalance);
}
