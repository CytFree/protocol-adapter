package com.example.cyt.demo.protocol.adapter.handler.server;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Data;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Http 服务入站处理
 * @Date 2019-07-12 10:06
 */
@Data
public class HttpServerChannelInboundHandler extends AbstractServerChannelInboundHandler {
    public HttpServerChannelInboundHandler(ChannelAdapter channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected String preHandle(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent);
        System.out.println(Thread.currentThread().getName() + "--Http Req Content:" + strContent);
        //TODO 接入方接收数据处理插件
        return strContent;
    }

    @Override
    protected void writeResp(ChannelHandlerContext ctx, ByteBuf respConvertBuf) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, respConvertBuf);
        if (respConvertBuf != null) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        }
        ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE);
    }
}