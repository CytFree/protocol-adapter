package com.example.cyt.demo.protocol.adapter.handler.initializer;

import com.example.cyt.demo.protocol.adapter.handler.server.TcpServerChannelInboundHandler;
import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.model.MessageDataStructConfig;
import com.example.cyt.demo.protocol.adapter.util.LengthFieldBasedFrameDecoderUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:53
 */
public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelAdapter channelAdapter;

    public TcpServerChannelInitializer(ChannelAdapter channelAdapter) {
        this.channelAdapter = channelAdapter;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterReqConfig().getMessageDataStructConfig();
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder =
                LengthFieldBasedFrameDecoderUtil.createLengthFieldBasedFrameDecoder(dataStructConfig);
        if (lengthFieldBasedFrameDecoder != null) {
            socketChannel.pipeline().addLast(lengthFieldBasedFrameDecoder);
        }
        socketChannel.pipeline().addLast(new StringDecoder(Charset.forName(channelAdapter.getAdapterReqConfig().getCharsetEncoding())));
        socketChannel.pipeline().addLast(new TcpServerChannelInboundHandler(channelAdapter));
    }
}
