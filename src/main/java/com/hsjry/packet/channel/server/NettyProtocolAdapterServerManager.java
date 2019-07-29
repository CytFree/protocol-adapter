package com.hsjry.packet.channel.server;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:37
 */
public class NettyProtocolAdapterServerManager implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyProtocolAdapterServerManager.class);
    private NettyProtocolAdapterServerFactory factory;

    public NettyProtocolAdapterServerManager(NettyProtocolAdapterServerFactory factory) {
        this.factory = factory;
    }

    public static Map<String, NettyProtocolAdapterServer> activeServer = new HashMap<>(8);
    public static Map<String, ChannelFuture> routerClient = new HashMap<>(8);

    public void startServer() {
        List<NettyProtocolAdapterServer> serverList = factory.createAllServer();
        for (NettyProtocolAdapterServer protocolAdapterServer : serverList) {
            boolean startSuccess = protocolAdapterServer.startServer();
            if (startSuccess) {
                activeServer.put(protocolAdapterServer.getChannelAdapter().getAdapterCode(), protocolAdapterServer);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (String key : activeServer.keySet()) {
                LOGGER.info(key + "适配器开始stopServer。。。");
                activeServer.get(key).stopServer();
            }
            activeServer.clear();
        }));
    }

    @Override
    public void afterPropertiesSet() {
        startServer();
    }

    /**
     * 1、从数据库加载适配器协议信息
     * 2、报文适配
     * 3、插件处理
     * 4、异常的处理
     */
    public static void main(String[] args) {
        NettyProtocolAdapterServerFactory factory = new NettyProtocolAdapterServerFactory();
        NettyProtocolAdapterServerManager manager = new NettyProtocolAdapterServerManager(factory);
        manager.startServer();
    }
}
