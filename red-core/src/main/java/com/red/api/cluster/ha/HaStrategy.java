package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.rpc.Request;
import com.red.api.rpc.Response;

public interface HaStrategy {
    Response call(Request request, LoadBalance loadBalance);
}
