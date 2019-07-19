//package com.example.cyt.demo.protocol.adapter.handler.initializer;
//
//import com.example.cyt.demo.protocol.adapter.handler.client.HttpClientInboundHandler;
//import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpRequestEncoder;
//import io.netty.handler.codec.http.HttpResponseDecoder;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.atomic.AtomicReference;
//
///**
// * @author by CYT
// * @Version 1.0
// * @Description TODO
// * @Date 2019-07-12 09:53
// */
//public class HttpClientChannelInitializer extends ChannelInitializer<SocketChannel> {
//    private ChannelAdapter channelAdapter;
//    private AtomicReference<ByteBuf> isFinishRouter;
//    private CountDownLatch countDownLatch;
//
//    public HttpClientChannelInitializer(ChannelAdapter channelAdapter,
//                                        AtomicReference<ByteBuf> isFinishRouter, CountDownLatch countDownLatch) {
//        this.channelAdapter = channelAdapter;
//        this.isFinishRouter = isFinishRouter;
//        this.countDownLatch = countDownLatch;
//    }
//
//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {));
//    }
//}
