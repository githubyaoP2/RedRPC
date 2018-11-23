package com.red.message;

import java.io.Serializable;

public enum MessageType  implements Serializable {
    ServerResponse(1),
    ClientRequest(2),
    PING(3),
    ACK(4);

    private int messageType;

    private MessageType(int messageType){
        this.messageType = messageType;
    }

    int getMessageType(){
        return messageType;
    }
}
