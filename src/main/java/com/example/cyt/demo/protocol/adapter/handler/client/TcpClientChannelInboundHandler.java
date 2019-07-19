package com.example.cyt.demo.protocol.adapter.handler.client;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.model.MessageDataStructConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static com.hsjry.packet.adaption.model.DataPacketModel.StructMode;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-15 09:52
 */
public class TcpClientChannelInboundHandler extends AbstractClientChannelInboundHandler {
    public TcpClientChannelInboundHandler(ChannelAdapter channelAdapter,
                                          AtomicReference<ByteBuf> isFinishRouter, CountDownLatch countDownLatch) {
        super(channelAdapter, isFinishRouter, countDownLatch);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String body = buf.toString(Charset.forName(channelAdapter.getAdapterRespConfig().getCharsetEncoding()));
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterReqConfig().getMessageDataStructConfig();
        //报文结构模式
        StructMode structMode = dataStructConfig.getRecvStructMode();
        if (StructMode.PLACEHOLDER_DATA.equals(structMode)) {
            body = body.replaceFirst(dataStructConfig.getRecvPlaceholder(), "");
        }
        System.out.println("CONTENT:" + body);
        isFinishRouter.set(Unpooled.copiedBuffer(body.getBytes()));
        countDownLatch.countDown();
        ctx.channel().closeFuture().addListeners(ChannelFutureListener.CLOSE);
    }
}