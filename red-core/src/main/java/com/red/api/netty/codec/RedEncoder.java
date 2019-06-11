package com.red.api.netty.codec;

import com.red.api.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RedEncoder extends MessageToByteEncoder<Response> {
    //序列化方式
    private String serialization;

    public RedEncoder(String serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) throws Exception {

    }
}
