package com.example.cyt.demo.protocol.adapter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-15 11:12
 */
public class NettyTcpClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //TODO 路由转发 TCP 协议，设置 handler
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf buf) throws Exception {
                        System.out.println("CONTENT:" + buf.toString(Charset.defaultCharset()));
                    }
                });
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 9012)).sync();
        if (channelFuture.isSuccess()) {
            Channel channel = channelFuture.channel();
            channel.writeAndFlush("你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好，中国你好");
        }
    }
}
