package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import com.red.api.message.ResponseMessage;

public interface HaStrategy {
    ResponseFuture call(RequestMessage requestMessage, LoadBalance loadBalance);
}
