package com.example.cyt.demo.protocol.adapter.server;

import com.example.cyt.demo.protocol.adapter.handler.initializer.TcpServerChannelInitializer;
import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import io.netty.channel.ChannelInitializer;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Tcp 服务
 * @Date 2019-07-12 14:54
 */
public class TcpNettyProtocolAdapterServer extends AbstractNettyProtocolAdapterServer {
    public TcpNettyProtocolAdapterServer(ChannelAdapter channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected ChannelInitializer createChannelInitializer() {
        return new TcpServerChannelInitializer(getChannelAdapter());
    }
}
