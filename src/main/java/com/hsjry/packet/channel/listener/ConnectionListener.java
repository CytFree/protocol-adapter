package com.hsjry.packet.channel.listener;

import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.server.NettyProtocolAdapterServer;
import com.hsjry.packet.channel.server.NettyProtocolAdapterServerManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-18 17:47
 */
public class ConnectionListener implements ChannelFutureListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionListener.class);

    private ChannelAdapterConfig channelAdapter;
    private CountDownLatch countDownLatch;
    private Long lastConnectionTime;

    public ConnectionListener(ChannelAdapterConfig channelAdapter, CountDownLatch countDownLatch, Long lastConnectionTime) {
        this.channelAdapter = channelAdapter;
        this.countDownLatch = countDownLatch;
        this.lastConnectionTime = lastConnectionTime;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        String adapterCode = channelAdapter.getAdapterCode();
        if (!channelFuture.isSuccess()) {
            if (System.currentTimeMillis() > lastConnectionTime) {
                LOGGER.error(adapterCode + "适配器，重连接出方失败，不再重试...");
                return;
            }
            final EventLoop loop = channelFuture.channel().eventLoop();
            LOGGER.error(adapterCode + "适配器，开始重连接出方...");
            NettyProtocolAdapterServer nettyServer = NettyProtocolAdapterServerManager.activeServer.get(adapterCode);
            if (nettyServer != null) {
                loop.schedule(() -> nettyServer.connectRespChannel(channelAdapter, countDownLatch, lastConnectionTime), 1L, TimeUnit.SECONDS);
            } else {
                LOGGER.error(adapterCode + "适配器对应的Netty服务已停止.");
            }
        } else {
            LOGGER.info(adapterCode + "适配器，连接接出方成功...");
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            NettyProtocolAdapterServerManager.routerClient.put(channelAdapter.getAdapterCode(), channelFuture);
        }
    }
}
