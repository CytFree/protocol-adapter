package com.hsjry.packet.channel.server;

import com.hsjry.packet.channel.enums.ProtocolTypeEnum;
import com.hsjry.packet.channel.listener.ConnectionListener;
import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.model.ChannelAdapterRespConfig;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import com.hsjry.packet.channel.model.NetworkProtocolConfig;
import com.hsjry.packet.channel.util.LengthFieldBasedFrameDecoderUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:37
 */
@Data
public abstract class AbstractNettyProtocolAdapterServer implements NettyProtocolAdapterServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionListener.class);

    /**
     * 渠道适配器
     */
    private ChannelAdapterConfig channelAdapter;

    /**
     * 服务是否运行中
     */
    private volatile boolean isRunning = false;

    /**
     * 处理Accept连接事件的线程，这里线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
     */
    private final EventLoopGroup boots = new NioEventLoopGroup(1);

    /**
     * 处理handler的工作线程，其实也就是处理IO读写 。线程数据默认为 CPU 核心数乘以2
     */
    private final EventLoopGroup workers = new NioEventLoopGroup();

    private final EventLoopGroup routerWorkers = new NioEventLoopGroup();

    public AbstractNettyProtocolAdapterServer(ChannelAdapterConfig channelAdapter) {
        this.channelAdapter = channelAdapter;
    }

    /**
     * 子类实现
     * 创建具体处理的ChannelInitializer
     *
     * @return
     */
    protected abstract ChannelInitializer createChannelInitializer();

    public final void init() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //初始化ServerBootstrap的线程组
        serverBootstrap.group(boots, workers);
        //设置将要被实例化的ServerChannel类
        serverBootstrap.channel(NioServerSocketChannel.class);
        //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
        serverBootstrap.childHandler(createChannelInitializer());
        NetworkProtocolConfig networkProtocolConfig = channelAdapter.getAdapterReqConfig().getNetworkProtocolConfig();
        if (networkProtocolConfig.getMaxActive() != null) {
            //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            serverBootstrap.option(ChannelOption.SO_BACKLOG, networkProtocolConfig.getMaxActive());
        }
        ChannelFuture channelFuture = serverBootstrap.bind(networkProtocolConfig.getPort()).sync();
        if (channelFuture.isSuccess()) {
            isRunning = true;
            LOGGER.info(getName() + "服务启动 成功，监控端口：" + networkProtocolConfig.getPort());
        } else {
            throw new RuntimeException(getName() + "服务启动 失败，监控端口：" + networkProtocolConfig.getPort());
        }
    }

    @Override
    public synchronized boolean startServer() {
        try {
            init();
            connectRespChannel(channelAdapter, null, null);
        } catch (InterruptedException e) {
            LOGGER.info(getName() + "服务启动 异常---------------" + e);
            return false;
        }
        return true;
    }

    @Override
    public synchronized boolean stopServer() {
        if (!isRunning) {
            LOGGER.info(this.getName() + " 未启动 .");
            return false;
        }
        try {
            Future future = workers.shutdownGracefully().await();
            if (!future.isSuccess()) {
                LOGGER.info(getName() + "-workerGroup 无法正常停止: " + future.cause());
            }
            future = boots.shutdownGracefully().await();
            if (!future.isSuccess()) {
                LOGGER.info(getName() + "-bootstrapGroup 无法正常停止: " + future.cause());
            }

            future = routerWorkers.shutdownGracefully().await();
            if (!future.isSuccess()) {
                LOGGER.info(getName() + "-routerWorkers 无法正常停止: " + future.cause());
            }
        } catch (Exception e) {
            LOGGER.info(getName() + "服务停止异常：" + e);
            return false;
        }
        LOGGER.info(getName() + "服务已经停止...");
        isRunning = false;
        return true;
    }

    @Override
    public synchronized void connectRespChannel(ChannelAdapterConfig channelAdapter, CountDownLatch countDownLatch,
                                                Long lastConnectionTime) {
        lastConnectionTime = lastConnectionTime == null ? System.currentTimeMillis() + 60 * 1000 : lastConnectionTime;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(routerWorkers).channel(NioSocketChannel.class);
        ChannelAdapterRespConfig respConfig = channelAdapter.getAdapterRespConfig();
        String respProtocolType = respConfig.getNetworkProtocolConfig().getProtocolType();
        if (ProtocolTypeEnum.HTTP.getCode().equals(respProtocolType)) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 将HTTP消息的多个部分合成一条完整的HTTP消息
                    ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                    // 客户端发送的是httpRequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                }
            });
        } else if (ProtocolTypeEnum.TCP.getCode().equals(respProtocolType)) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder(Charset.forName(respConfig.getCharsetEncoding())));
                    MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterRespConfig().getMessageDataStructConfig();
                    LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder =
                            LengthFieldBasedFrameDecoderUtil.createLengthFieldBasedFrameDecoder(dataStructConfig);
                    if (lengthFieldBasedFrameDecoder != null) {
                        ch.pipeline().addLast(lengthFieldBasedFrameDecoder);
                    }
                }
            });
        } else {
            throw new RuntimeException("暂不支持该协议: " + respProtocolType);
        }

        NetworkProtocolConfig networkProtocolConfig = respConfig.getNetworkProtocolConfig();
        String adapterCode = channelAdapter.getAdapterCode();
        try {
            bootstrap.connect(networkProtocolConfig.getIp(), networkProtocolConfig.getPort())
                    .addListeners(new ConnectionListener(channelAdapter, countDownLatch, lastConnectionTime));
        } catch (Exception e) {
            LOGGER.error(adapterCode + "适配器，连接接出方失败：" + e);
        }
    }

    private String getName() {
        String adapterCode = channelAdapter.getAdapterCode();
        String adapterName = channelAdapter.getAdapterName();
        String protocolType = channelAdapter.getAdapterReqConfig().getNetworkProtocolConfig().getProtocolType();
        return adapterCode + "-" + adapterName + "-" + protocolType + "-Netty-Server";
    }
}
