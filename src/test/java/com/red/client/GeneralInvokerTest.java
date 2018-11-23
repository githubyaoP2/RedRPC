package com.red.client;

import com.red.message.MessageType;
import com.red.message.RedMessage;
import com.red.message.RequestMsg;
import org.junit.Test;

public class GeneralInvokerTest {
    @Test
    public void test(){
        GeneralInvoker generalInvoker = new GeneralInvoker();
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setInstanceName("com.red.tmp.AHello");
        requestMsg.setMethodName("hello");
        generalInvoker.invoke(new RedMessage(requestMsg,MessageType.ClientRequest));
    }
}
