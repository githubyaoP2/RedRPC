package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseFuture;
import com.red.api.message.ResponseMessage;
import com.red.api.rpc.Referer;

/**
 * 高可用策略 -- 快速失败
 */
public class FailFastHaStrategy implements HaStrategy{
    @Override
    public ResponseFuture call(RequestMessage requestMessage, LoadBalance loadBalance) {
        Referer referer = loadBalance.select(requestMessage);
        return referer.request(requestMessage);
    }
}
