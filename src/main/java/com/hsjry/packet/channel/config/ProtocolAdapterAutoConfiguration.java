package com.hsjry.packet.channel.config;

import com.hsjry.packet.channel.server.NettyProtocolAdapterServerFactory;
import com.hsjry.packet.channel.server.NettyProtocolAdapterServerManager;
import org.springframework.context.annotation.Bean;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-27 10:26
 */
public class ProtocolAdapterAutoConfiguration {
    @Bean
    public NettyProtocolAdapterServerFactory nettyProtocolAdapterServerFactory() {
        return new NettyProtocolAdapterServerFactory();
    }

    @Bean
    public NettyProtocolAdapterServerManager nettyProtocolAdapterServerManager(NettyProtocolAdapterServerFactory factory) {
        return new NettyProtocolAdapterServerManager(factory);
    }
}
