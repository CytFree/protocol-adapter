package com.hsjry.packet.channel.config;

import com.hsjry.packet.channel.server.NettyProtocolAdapterServerFactory;
import com.hsjry.packet.channel.server.NettyProtocolAdapterServerManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-27 10:26
 */
public class ProtocolAdapterAutoConfiguration {
    @Value("${length.zone.length.type:byte}")
    private String lengthZoneLengthType;

    @Bean
    public NettyProtocolAdapterServerFactory nettyProtocolAdapterServerFactory() {
        return new NettyProtocolAdapterServerFactory(lengthZoneLengthType);
    }

    @Bean
    public NettyProtocolAdapterServerManager nettyProtocolAdapterServerManager(NettyProtocolAdapterServerFactory factory) {
        return new NettyProtocolAdapterServerManager(factory);
    }
}
