package com.red.api.netty.codec;

import com.red.api.message.RedMessage;
import com.red.api.message.ResponseMessage;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.zookeeper.server.ByteBufferOutputStream;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class RedEncoder extends MessageToByteEncoder<RedMessage> {

    private final String REDV1 = "REDRPC1";

    //序列化方式
    private String serialization;

    public RedEncoder(String serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RedMessage response, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(REDV1.getBytes());
        switch (serialization){
            case "jdk":
                ByteBuf byteBuf1 = channelHandlerContext.alloc().buffer();//此缓冲区并非用来写通道，所以申请堆缓冲区
                ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf1);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteBufOutputStream);
                objectOutputStream.writeObject(response);
                int len = byteBuf1.readableBytes();
                byteBuf.writeInt(len);
                byteBuf.writeBytes(byteBuf1);
                break;
            case "json":
                break;
        }
    }
}
