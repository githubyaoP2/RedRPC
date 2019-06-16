package com.red.api.cluster.ha;

import com.red.api.cluster.loadBalance.LoadBalance;
import com.red.api.message.RequestMessage;
import com.red.api.message.ResponseMessage;

/**
 * 高可用策略 -- 快速失败
 */
public class FailFastHaStrategy implements HaStrategy{
    @Override
    public ResponseMessage call(RequestMessage requestMessage, LoadBalance loadBalance) {
        return null;
    }
}
