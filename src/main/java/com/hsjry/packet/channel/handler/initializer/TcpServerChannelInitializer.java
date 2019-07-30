package com.hsjry.packet.channel.handler.initializer;

import com.hsjry.packet.channel.handler.server.TcpServerChannelInboundHandler;
import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import com.hsjry.packet.channel.util.LengthFieldBasedFrameDecoderUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 09:53
 */
public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelAdapterConfig channelAdapter;

    public TcpServerChannelInitializer(ChannelAdapterConfig channelAdapter) {
        this.channelAdapter = channelAdapter;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterReqConfig().getMessageDataStructConfig();
        ByteToMessageDecoder byteToMessageDecoder =
                LengthFieldBasedFrameDecoderUtil.createLengthFieldBasedFrameDecoder(dataStructConfig);
        if (byteToMessageDecoder != null) {
            socketChannel.pipeline().addLast(byteToMessageDecoder);
        }
        socketChannel.pipeline().addLast(new StringDecoder(Charset.forName(channelAdapter.getAdapterReqConfig().getCharsetEncoding())));
        socketChannel.pipeline().addLast(new TcpServerChannelInboundHandler(channelAdapter));
    }
}
