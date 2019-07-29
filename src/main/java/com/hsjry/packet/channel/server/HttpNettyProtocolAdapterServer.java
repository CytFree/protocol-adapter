package com.hsjry.packet.channel.server;

import com.hsjry.packet.channel.handler.initializer.HttpServerChannelInitializer;
import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import io.netty.channel.ChannelInitializer;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Http 服务
 * @Date 2019-07-12 14:54
 */
public class HttpNettyProtocolAdapterServer extends AbstractNettyProtocolAdapterServer {
    public HttpNettyProtocolAdapterServer(ChannelAdapterConfig channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected ChannelInitializer createChannelInitializer() {
        return new HttpServerChannelInitializer(getChannelAdapter());
    }
}
