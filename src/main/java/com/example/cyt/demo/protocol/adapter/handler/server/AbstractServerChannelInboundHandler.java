package com.example.cyt.demo.protocol.adapter.handler.server;

import com.example.cyt.demo.protocol.adapter.enums.ProtocolTypeEnum;
import com.example.cyt.demo.protocol.adapter.handler.client.HttpClientChannelInboundHandler;
import com.example.cyt.demo.protocol.adapter.handler.client.TcpClientChannelInboundHandler;
import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.model.NetworkProtocolConfig;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServer;
import com.example.cyt.demo.protocol.adapter.server.NettyProtocolAdapterServerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.Data;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.cyt.demo.protocol.adapter.util.MessageDataStructConvert.dataStructConvert;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty 服务入站处理
 * <p>
 * 1、请求入站，解析接入方请求报文数据结构，获取真实请求数据
 * 2、报文转换成接出方需要的数据格式
 * 3、组装接出方需要的接收报文数据结构
 * 4、解析接出方响应报文数据结构，获取真实响应数据
 * 5、报文转换成接入方需要的数据格式
 * 6、组装接入方需要的接收报文数据结构
 * </p>
 * @Date 2019-07-17 10:01
 */
@Data
public abstract class AbstractServerChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private static final String HTTP_CLIENT_CHANNEL_HANDLER = "HttpClientChannelInboundHandler";
    private static final String TCP_CLIENT_CHANNEL_HANDLER = "TcpClientChannelInboundHandler";

    protected ChannelAdapter channelAdapter;
    protected ChannelFuture connectFuture;
    protected AtomicReference<String> isFinishRouter = new AtomicReference<>(null);
    protected CountDownLatch countDownLatch = new CountDownLatch(1);

    public AbstractServerChannelInboundHandler(ChannelAdapter channelAdapter) {
        this.channelAdapter = channelAdapter;
    }

    /**
     * 前置处理
     *
     * @param ctx
     * @param msg
     * @return
     */
    protected abstract String preHandle(ChannelHandlerContext ctx, Object msg);

    private ByteBuf commonHandle(String body) {
        try {
            //TODO 报文转换（接入方数据格式转换为接出方需要的数据格式）
            //TODO 接出方接收数据处理插件
            //进行报文数据结构转换（转为接出方所需的报文数据结构格式）
            ByteBuf respRecvBuf = dataStructConvert(
                    body, channelAdapter.getAdapterRespConfig().getCharsetEncoding(),
                    channelAdapter.getAdapterRespConfig().getMessageDataStructConfig());

            //路由转发
            router(respRecvBuf);
            //获取接出方响应
            String respSend = isFinishRouter.get();

            //TODO 报文转换（接出方数据格式转换为接入方需要的格式）
            //TODO 接入方发送数据处理插件
            //接入响应
            System.out.println("new content:" + respSend);
            ByteBuf respConvertBuf = dataStructConvert(
                    respSend, channelAdapter.getAdapterReqConfig().getCharsetEncoding(),
                    channelAdapter.getAdapterReqConfig().getMessageDataStructConfig());
            return respConvertBuf;
        } catch (Exception e) {
            throw new RuntimeException("报文解析异常：" + e);
        }
    }

    /**
     * 写响应
     *
     * @param ctx
     * @param respConvertBuf
     */
    protected abstract void writeResp(ChannelHandlerContext ctx, ByteBuf respConvertBuf);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = preHandle(ctx, msg);
        ByteBuf respConvertBuf = commonHandle(body);
        writeResp(ctx, respConvertBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.toString());
        ctx.close();
    }

    protected void router(ByteBuf buf) {
        try {
            connectFuture = NettyProtocolAdapterServerManager.routerClient.get(channelAdapter.getAdapterCode());
            while (!connectFuture.channel().isActive()) {
                NettyProtocolAdapterServer nettyServer = NettyProtocolAdapterServerManager.activeServer.get(channelAdapter.getAdapterCode());
                CountDownLatch retryConnCotDownLatch = new CountDownLatch(1);
                nettyServer.respChannelActive(channelAdapter, retryConnCotDownLatch);
                Long time = channelAdapter.getAdapterRespConfig().getNetworkProtocolConfig().getTimeOut();
                if (time == null) {
                    retryConnCotDownLatch.await();
                } else {
                    retryConnCotDownLatch.await(time, TimeUnit.MILLISECONDS);
                }
                connectFuture = NettyProtocolAdapterServerManager.routerClient.get(channelAdapter.getAdapterCode());
            }

            Channel channel = connectFuture.channel();
            NetworkProtocolConfig networkProtocolConfig = channelAdapter.getAdapterRespConfig().getNetworkProtocolConfig();
            String respProtocolType = networkProtocolConfig.getProtocolType();
            if (ProtocolTypeEnum.HTTP.getCode().equals(respProtocolType)) {
                DefaultFullHttpRequest request = createHttpRequest(networkProtocolConfig, buf);
                // 发送http请求
                if (channel.pipeline().get(HTTP_CLIENT_CHANNEL_HANDLER) != null) {
                    channel.pipeline().remove(HTTP_CLIENT_CHANNEL_HANDLER);
                }
                channel.pipeline().addLast(HTTP_CLIENT_CHANNEL_HANDLER,
                        new HttpClientChannelInboundHandler(channelAdapter, isFinishRouter, countDownLatch));
                channel.writeAndFlush(request);
            } else if (ProtocolTypeEnum.TCP.getCode().equals(respProtocolType)) {
                // 发送Tcp请求
                if (channel.pipeline().get(TCP_CLIENT_CHANNEL_HANDLER) != null) {
                    channel.pipeline().remove(TCP_CLIENT_CHANNEL_HANDLER);
                }
                channel.pipeline().addLast(TCP_CLIENT_CHANNEL_HANDLER,
                        new TcpClientChannelInboundHandler(channelAdapter, isFinishRouter, countDownLatch));
                channel.writeAndFlush(buf);
            }

            //超时时间
            Long timeOut = channelAdapter.getAdapterRespConfig().getNetworkProtocolConfig().getTimeOut();
            if (timeOut != null) {
                countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
            } else {
                countDownLatch.await();
            }
            if (isFinishRouter.get() == null) {
                throw new RuntimeException("路由转发，接出方响应超时");
            } else {
                System.out.println("路由转发，完成响应");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 异常处理
        }
    }

    /**
     * 构建 Http 请求
     *
     * @param networkProtocolConfig
     * @param buf
     * @return
     * @throws Exception
     */
    private DefaultFullHttpRequest createHttpRequest(NetworkProtocolConfig networkProtocolConfig, ByteBuf buf) throws Exception {
        String path = networkProtocolConfig.getPath() == null ? "" : networkProtocolConfig.getPath();
        path = path.startsWith("/") ? path : ("/" + path);
        URI uri = new URI("http://" + networkProtocolConfig.getIp() + ":" + networkProtocolConfig.getPort() + path);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(), buf);

        // 构建http请求
        request.headers().set(HttpHeaderNames.HOST, networkProtocolConfig.getIp());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        return request;
    }
}
