package com.example.cyt.demo.protocol.adapter.handler.server;

import com.example.cyt.demo.protocol.adapter.model.ChannelAdapter;
import com.example.cyt.demo.protocol.adapter.model.MessageDataStructConfig;
import com.hsjry.packet.adaption.model.DataPacketModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Tcp 服务入站处理
 * @Date 2019-07-12 10:06
 */
@Data
public class TcpServerChannelInboundHandler extends AbstractServerChannelInboundHandler {
    public TcpServerChannelInboundHandler(ChannelAdapter channelAdapter) {
        super(channelAdapter);
    }

    @Override
    protected String preHandle(ChannelHandlerContext ctx, Object msg) {
        String body = (String) msg;
        MessageDataStructConfig dataStructConfig = channelAdapter.getAdapterReqConfig().getMessageDataStructConfig();
        //报文结构模式
        DataPacketModel.StructMode structMode = dataStructConfig.getRecvStructMode();
        if (DataPacketModel.StructMode.PLACEHOLDER_DATA.equals(structMode)) {
            body = body.replaceFirst(dataStructConfig.getRecvPlaceholder(), "");
        }
        System.out.println("Tcp Req Content:" + body);
        //TODO 接入方接收数据处理插件
        return body;
    }

    @Override
    protected void writeResp(ChannelHandlerContext ctx, ByteBuf respConvertBuf) {
        ctx.writeAndFlush(respConvertBuf).addListeners(ChannelFutureListener.CLOSE);
    }
}