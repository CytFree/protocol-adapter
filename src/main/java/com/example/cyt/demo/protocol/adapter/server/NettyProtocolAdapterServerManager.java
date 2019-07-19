package com.example.cyt.demo.protocol.adapter.server;

import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:37
 */
@Component
public class NettyProtocolAdapterServerManager implements InitializingBean {
    public static Map<String, NettyProtocolAdapterServer> activeServer = new HashMap<>(8);
    public static Map<String, ChannelFuture> routerClient = new HashMap<>(8);

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    public static void startServer() {
        List<NettyProtocolAdapterServer> serverList = NettyProtocolAdapterServerFactory.createAllServer();
        for (NettyProtocolAdapterServer protocolAdapterServer : serverList) {
            boolean startSuccess = protocolAdapterServer.startServer();
            if (startSuccess) {
                activeServer.put(protocolAdapterServer.getChannelAdapter().getAdapterCode(), protocolAdapterServer);
            }
        }
    }

    public static void main(String[] args) {
        startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (String key : activeServer.keySet()) {
                System.out.println(key + "适配器开始stopServer。。。");
                activeServer.get(key).stopServer();
            }
            activeServer.clear();
        }));
    }
}
