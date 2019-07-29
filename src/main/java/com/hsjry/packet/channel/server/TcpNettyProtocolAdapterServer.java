package com.hsjry.packet.channel.server;

import com.hsjry.packet.channel.handler.initializer.TcpServerChannelInitializer;
import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import io.netty.channel.ChannelInitializer;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Tcp 服务
 * @Date 2019-07-12 14:54
 */
public class TcpNettyProtocolAdapterServer extends AbstractNettyProtocolAdapterServer {
    public TcpNettyProtocolAdapterServer(ChannelAdapterConfig channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected ChannelInitializer createChannelInitializer() {
        return new TcpServerChannelInitializer(getChannelAdapter());
    }
}
