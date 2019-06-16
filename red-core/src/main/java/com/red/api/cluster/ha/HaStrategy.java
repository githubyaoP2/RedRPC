package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseMessage;

public interface HaStrategy {
    ResponseMessage call(RequestMessage requestMessage, LoadBalance loadBalance);
}
