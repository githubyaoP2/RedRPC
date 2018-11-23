package com.red.message;

import java.io.Serializable;

public class RedMessage implements Serializable {
    Message message;
    MessageType messageType;

    public RedMessage(Message message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public Message getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
