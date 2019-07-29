package com.hsjry.packet.channel.handler.server;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.model.ChannelAdapterConfig;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hsjry.packet.channel.util.MessageDataStructConvert.dataStructConvert;

/**
 * @author by CYT
 * @Version 1.0
 * @Description Netty Tcp 服务入站处理
 * @Date 2019-07-12 10:06
 */
public class TcpServerChannelInboundHandler extends AbstractServerChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerChannelInboundHandler.class);

    public TcpServerChannelInboundHandler(ChannelAdapterConfig channelAdapter) {
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
        LOGGER.info("Tcp Req Content:" + body);
        //TODO 接入方接收数据处理插件
        return body;
    }

    @Override
    protected void writeResp(ChannelHandlerContext ctx, String resp) throws Exception {
        //TODO 接入方发送数据处理插件
        //将响应报文包装成接入方需要的数据格式，并转为字节
        ByteBuf respConvertBuf = dataStructConvert(
                resp, channelAdapter.getAdapterReqConfig().getCharsetEncoding(),
                channelAdapter.getAdapterReqConfig().getMessageDataStructConfig());
        ctx.writeAndFlush(respConvertBuf).addListeners(ChannelFutureListener.CLOSE);
    }
}