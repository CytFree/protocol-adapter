package com.hsjry.packet.channel.handler.client;

import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import com.hsjry.packet.channel.util.HttpContentUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-15 09:52
 */
public class HttpClientChannelInboundHandler extends AbstractClientChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientChannelInboundHandler.class);

    public HttpClientChannelInboundHandler(ChannelAdapterConfig channelAdapter,
                                           AtomicReference<String> isFinishRouter, CountDownLatch countDownLatch) {
        super(channelAdapter, isFinishRouter, countDownLatch);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        LOGGER.info("CONTENT_TYPE:" + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
        LOGGER.info(Thread.currentThread().getName() + "：" + isFinishRouter.toString() + "-----" + countDownLatch.toString());

        String charsetName = channelAdapter.getAdapterRespConfig().getCharsetEncoding();
        ByteBuf buf = response.content();
        LOGGER.info("resp Len: " + buf.readableBytes() + "\n" + "origin CONTENT:" + buf.toString(Charset.forName(charsetName)));
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterRespConfig().getMessageDataStructConfig();
        HttpContentUtil.getBodyFromHttpContext(buf, dataStructConfig);
        String body = buf.toString(Charset.forName(charsetName));
        //TODO 接出方发送数据处理插件
        isFinishRouter.set(body);
        countDownLatch.countDown();
        ctx.channel().closeFuture().addListeners(ChannelFutureListener.CLOSE);
    }
}