package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.rpc.Request;
import com.red.api.rpc.Response;

public class FailFastHaStrategy implements HaStrategy{
    @Override
    public Response call(Request request, LoadBalance loadBalance) {
        return null;
    }
}
