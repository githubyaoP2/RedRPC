package com.red.api.netty.codec;

import com.red.api.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RedEncoder extends MessageToByteEncoder<ResponseMessage> {
    //序列化方式
    private String serialization;

    public RedEncoder(String serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage response, ByteBuf byteBuf) throws Exception {

    }
}
