package com.example.cyt.demo.protocol.adapter.listener;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServer;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServerManager;
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

    private ChannelAdapter channelAdapter;
    private CountDownLatch countDownLatch;

    public ConnectionListener(ChannelAdapter channelAdapter, CountDownLatch countDownLatch) {
        this.channelAdapter = channelAdapter;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        String adapterCode = channelAdapter.getAdapterCode();
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            LOGGER.error(adapterCode + "适配器，开始重连接出方...");
            NettyProtocolAdapterServer nettyServer = NettyProtocolAdapterServerManager.activeServer.get(adapterCode);
            if (nettyServer != null) {
                loop.schedule(() -> nettyServer.respChannelActive(channelAdapter, countDownLatch), 1L, TimeUnit.SECONDS);
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
