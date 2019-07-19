package com.example.cyt.demo.protocol.adapter.handler.client;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.model.MessageDataStructConfig;
import com.hsjry.packet.adaption.model.DataPacketModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

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
    public HttpClientChannelInboundHandler(ChannelAdapter channelAdapter,
                                           AtomicReference<ByteBuf> isFinishRouter, CountDownLatch countDownLatch) {
        super(channelAdapter, isFinishRouter, countDownLatch);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("CONTENT_TYPE:" + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
        }
        if (msg instanceof HttpContent) {
            System.out.println(Thread.currentThread().getName() + "：" + isFinishRouter.toString() + "-----" + countDownLatch.toString());

            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            System.out.println("Len: " + buf.readableBytes() + "\n" + "CONTENT:" + buf.toString(Charset.defaultCharset()));
            MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterRespConfig().getMessageDataStructConfig();
            //响应数据结构
            DataPacketModel.StructMode structMode = dataStructConfig.getSendStructMode();
            switch (structMode) {
                case DATA:
                    break;
                case LENGTH_DATA:
                    //长度 + 数据
                    //长度域长度
                    Integer lengthZoneLength = dataStructConfig.getSendLengthZoneLength();
                    byte[] lengthBody = new byte[lengthZoneLength];
                    buf.readBytes(lengthBody, 0, lengthBody.length);
                    break;
                case LENGTH_PLACEHOLDER_DATA:
                    lengthZoneLength = dataStructConfig.getSendLengthZoneLength();
                    Integer placeHolderZoneLength = dataStructConfig.getSendPlaceholder().length();
                    lengthBody = new byte[lengthZoneLength + placeHolderZoneLength];
                    buf.readBytes(lengthBody, 0, lengthBody.length);
                    break;
                case PLACEHOLDER_DATA:
                    //占位符 + 数据
                    //占位符长度
                    placeHolderZoneLength = dataStructConfig.getSendPlaceholder().length();
                    lengthBody = new byte[placeHolderZoneLength];
                    buf.readBytes(lengthBody, 0, lengthBody.length);
                    break;
                case PLACEHOLDER_LENGTH_DATA:
                    lengthZoneLength = dataStructConfig.getSendLengthZoneLength();
                    placeHolderZoneLength = dataStructConfig.getSendPlaceholder().length();
                    lengthBody = new byte[lengthZoneLength + placeHolderZoneLength];
                    buf.readBytes(lengthBody, 0, lengthBody.length);
                    break;
                default:
            }
            isFinishRouter.set(buf);
            countDownLatch.countDown();
            ctx.channel().closeFuture().addListeners(ChannelFutureListener.CLOSE);
        }
    }
}