package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import com.red.api.message.ResponseMessage;

/**
 * 高可用策略，循环调用
 */
public class FailOverHaStrategy implements HaStrategy{
    @Override
    public ResponseFuture call(RequestMessage requestMessage, LoadBalance loadBalance) {
        return null;
    }
}
