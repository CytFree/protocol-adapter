package com.hsjry.packet.channel.handler.server;

import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import com.hsjry.packet.channel.util.HttpContentUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

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
        String charsetName = channelAdapter.getAdapterReqConfig().getCharsetEncoding();
        LOGGER.info("req Len: " + content.readableBytes() + "\n" + "origin CONTENT:" + content.toString(Charset.forName(charsetName)));
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterReqConfig().getMessageDataStructConfig();
        HttpContentUtil.getBodyFromHttpContext(content, dataStructConfig);
        String body = content.toString(Charset.forName(charsetName));
        LOGGER.info(Thread.currentThread().getName() + "--Http Req Content:" + body);
        //TODO 接入方接收数据处理插件
        return body;
    }

    @Override
    protected void writeResp(ChannelHandlerContext ctx, String resp) throws Exception {
        //TODO 接入方发送数据处理插件
        //将响应报文包装成接入方需要的数据格式，并转为字节
        ByteBuf respConvertBuf = dataStructConvert(
                resp, channelAdapter.getAdapterReqConfig().getCharsetEncoding(),
                channelAdapter.getAdapterReqConfig().getMessageDataStructConfig());
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, respConvertBuf);
        response.headers().set(CONNECTION, KEEP_ALIVE);
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE);
    }
}