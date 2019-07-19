package com.example.cyt.demo.protocol.adapter.handler.initializer;

import com.example.cyt.demo.protocol.adapter.handler.server.HttpServerChannelInboundHandler;
import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:53
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelAdapter channelAdapter;

    public HttpServerChannelInitializer(ChannelAdapter channelAdapter) {
        this.channelAdapter = channelAdapter;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 服务端接收到的是HttpRequest请求，所以要使用HttpRequestDecoder进行解码
        ch.pipeline().addLast(new HttpRequestDecoder());
        // 将HTTP消息的多个部分合成一条完整的HTTP消息
        ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        // 服务端发送的是HttpResponse响应，所以要使用HttpResponseEncoder进行编码
        ch.pipeline().addLast(new HttpResponseEncoder());
        // 解决大码流的问题，ChunkedWriteHandler：向客户端发送HTML5文件
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new HttpServerChannelInboundHandler(channelAdapter));
    }
}
