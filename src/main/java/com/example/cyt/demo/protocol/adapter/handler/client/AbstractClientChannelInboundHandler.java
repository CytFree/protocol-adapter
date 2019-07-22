package com.example.cyt.demo.protocol.adapter.handler.client;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServer;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-15 09:52
 */
public class AbstractClientChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientChannelInboundHandler.class);

    protected AtomicReference<String> isFinishRouter;
    protected ChannelAdapter channelAdapter;
    protected CountDownLatch countDownLatch;

    public AbstractClientChannelInboundHandler(ChannelAdapter channelAdapter,
                                               AtomicReference<String> isFinishRouter, CountDownLatch countDownLatch) {
        this.channelAdapter = channelAdapter;
        this.isFinishRouter = isFinishRouter;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String getAdapterCode = channelAdapter.getAdapterCode();
        LOGGER.error(getAdapterCode + "适配器，接出方失去连接，开始重连操作...");
        NettyProtocolAdapterServer nettyServer = NettyProtocolAdapterServerManager.activeServer.get(getAdapterCode);
        if (nettyServer != null) {
            ctx.channel().eventLoop().schedule(() -> nettyServer.respChannelActive(channelAdapter, null), 1, TimeUnit.SECONDS);
        } else {
            LOGGER.error(getAdapterCode + "适配器对应的Netty服务已停止.");
        }
    }
}