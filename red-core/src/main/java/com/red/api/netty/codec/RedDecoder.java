package com.red.api.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ObjectInputStream;
import java.util.List;

public class RedDecoder extends ByteToMessageDecoder {

    private final String REDV1 = "REDRPC1";

    //序列化方式
    private String serialization;

    public RedDecoder(String serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while(true){
            if(byteBuf.readableBytes() > 7){
                byte[] version = new byte[7];
                byteBuf.readBytes(version);
                if(REDV1.equals(new String(version)) && byteBuf.readableBytes() > 4){
                    int len = byteBuf.readInt();
                    if(len>0 && byteBuf.readableBytes()>=len){
                        byte[] message = new byte[len];
                        byteBuf.readBytes(message);
                        ByteBuf byteBuf1 = channelHandlerContext.alloc().buffer();
                        byteBuf1.writeBytes(message);
                        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(byteBuf1);
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteBufInputStream);
                        list.add(objectInputStream.readObject());
                    }else {
                        break;
                    }
                }else {
                    break;
                }
            }else {
                break;
            }
        }
    }
}
