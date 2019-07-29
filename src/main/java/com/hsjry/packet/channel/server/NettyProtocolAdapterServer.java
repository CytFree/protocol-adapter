package com.hsjry.packet.channel.server;

import com.hsjry.packet.channel.model.ChannelAdapterConfig;

import java.util.concurrent.CountDownLatch;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:37
 */
public interface NettyProtocolAdapterServer {
    /**
     * 获取渠道适配器
     *
     * @return
     */
    ChannelAdapterConfig getChannelAdapter();

    /**
     * 开启服务
     *
     * @return
     */
    boolean startServer();

    /**
     * 停止服务
     *
     * @return
     */
    boolean stopServer();

    /**
     * 创建路由通道
     *
     * @param channelAdapter
     * @param countDownLatch
     * @param lastConnectionTime 重连超时时间
     */
    void connectRespChannel(ChannelAdapterConfig channelAdapter, CountDownLatch countDownLatch, Long lastConnectionTime);
}
