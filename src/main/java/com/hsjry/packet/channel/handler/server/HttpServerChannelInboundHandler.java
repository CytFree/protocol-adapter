package com.hsjry.packet.channel.handler.server;

import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hsjry.packet.channel.util.MessageDataStructConvert.dataStructConvert;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Http 服务入站处理
 * @Date 2019-07-12 10:06
 */
public class HttpServerChannelInboundHandler extends AbstractServerChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerChannelInboundHandler.class);

    public HttpServerChannelInboundHandler(ChannelAdapterConfig channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected String preHandle(ChannelHandlerContext ctx, Object msg) {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent);
        LOGGER.info(Thread.currentThread().getName() + "--Http Req Content:" + strContent);
        //TODO 接入方接收数据处理插件
        return strContent;
    }

    @Override
    protected void writeResp(ChannelHandlerContext ctx, String resp) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //TODO 接入方发送数据处理插件
        //将响应报文包装成接入方需要的数据格式，并转为字节
        ByteBuf respConvertBuf = dataStructConvert(
                resp, channelAdapter.getAdapterReqConfig().getCharsetEncoding(),
                channelAdapter.getAdapterReqConfig().getMessageDataStructConfig());
        if (respConvertBuf != null) {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        }
        response.replace(respConvertBuf);
        ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE);
    }
}