package com.red.client;

import com.red.message.ResponseMsg;
import com.red.tmp.AHello;
import com.red.tmp.BHello;
import org.junit.Test;

public class RedClientTest {
    @Test
    public void test(){
     //  RedClient client = new RedClient();
        AHello aHello = new AHello(); aHello.value =2;
        BHello bHello = new BHello(); bHello.value =3;
        ResponseMsg responseMsg = RedClient.executeSync("com.red.tmp.CHello","compute",aHello,bHello);
        System.out.println(responseMsg.getResult());
    }
}
