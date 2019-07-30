package com.hsjry.packet.channel.util;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import com.hsjry.packet.channel.server.NettyProtocolAdapterServerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-16 14:57
 */
public class MessageDataStructConvert {
    public static ByteBuf dataStructConvert(String body, String charsetName, MessageDataStructConfig dataStructConfig)
            throws UnsupportedEncodingException {
        Integer boundSide = dataStructConfig.getBoundSide();
        //数据结构
        DataPacketModel.StructMode structMode;
        //数据长度域结构
        DataPacketModel.LengthMode lengthMode;
        //数据长度域的长度
        Integer lengthZoneLength;
        //占位符
        String placeHolder;
        //数据长度
        int dataLength = body.getBytes(charsetName).length;
        if (boundSide == 0) {
            //接入响应，报文格式转为接入方的数据发送格式
            structMode = dataStructConfig.getSendStructMode();
            lengthMode = dataStructConfig.getSendLengthMode();
            lengthZoneLength = dataStructConfig.getSendLengthZoneLength();
            placeHolder = dataStructConfig.getSendPlaceholder();
        } else {
            //接出接收，报文格式转为接出方的数据接收格式
            structMode = dataStructConfig.getRecvStructMode();
            lengthMode = dataStructConfig.getRecvLengthMode();
            lengthZoneLength = dataStructConfig.getRecvLengthZoneLength();
            placeHolder = dataStructConfig.getRecvPlaceholder();
        }

        if (structMode != null) {
            switch (structMode) {
                case DATA:
                    return Unpooled.wrappedBuffer(body.getBytes(charsetName));
                case LENGTH_DATA:
                    int totalLen = getLen(lengthMode, dataLength, 0, lengthZoneLength);
                    ByteBuf buf = Unpooled.buffer(lengthZoneLength + dataLength);
                    writeLen(dataStructConfig, buf, totalLen, lengthZoneLength);
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                case LENGTH_PLACEHOLDER_DATA:
                    totalLen = getLen(lengthMode, dataLength, placeHolder.length(), lengthZoneLength);
                    buf = Unpooled.buffer(lengthZoneLength + dataLength + placeHolder.length());
                    writeLen(dataStructConfig, buf, totalLen, lengthZoneLength);
                    buf.writeBytes(placeHolder.getBytes(charsetName));
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                case PLACEHOLDER_DATA:
                    return Unpooled.wrappedBuffer((placeHolder + body).getBytes(charsetName));
                case PLACEHOLDER_LENGTH_DATA:
                    totalLen = getLen(lengthMode, dataLength, placeHolder.length(), lengthZoneLength);
                    buf = Unpooled.buffer(lengthZoneLength + dataLength + placeHolder.length());
                    buf.writeBytes(placeHolder.getBytes(charsetName));
                    writeLen(dataStructConfig, buf, totalLen, lengthZoneLength);
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                default:
            }
        }
        return Unpooled.wrappedBuffer(body.getBytes(charsetName));
    }

    private static void writeLen(MessageDataStructConfig dataStructConfig, ByteBuf buf, int totalLen, int lengthZoneLength) {
        String lengthZoneLengthType = dataStructConfig.getLengthZoneLengthType();
        if (NettyProtocolAdapterServerFactory.LengthZoneLengthType.BYTE.name().equals(lengthZoneLengthType)) {
            writeByteLen(buf, totalLen, lengthZoneLength);
        } else {
            DataPacketModel.LengthZoneFillMode lengthZoneFillMode = dataStructConfig.getSendLengthZoneFillMode();
            writeStringLen(lengthZoneFillMode, buf, totalLen, lengthZoneLength);
        }
    }

    private static void writeStringLen(DataPacketModel.LengthZoneFillMode lengthZoneFillMode,
                                       ByteBuf buf, int totalLen, int lengthZoneLength) {
        String lenStr = String.valueOf(totalLen);
        if (lengthZoneFillMode == null) {
            throw new EncoderException("发送数据长度域补齐方式不能为空");
        }
        if (lenStr.length() > lengthZoneLength) {
            throw new EncoderException(
                    "不支持的长度域长度: " + lengthZoneLength + " (期望值: 长度域的长度大于等于报文长度值)");
        }

        StringBuffer sb = new StringBuffer();
        int subInt = lengthZoneLength - lenStr.length();
        switch (lengthZoneFillMode) {
            //左边补0
            case LEFT_ZERO:
                for (int i = 0; i < subInt; i++) {
                    sb.append(NumberUtils.INTEGER_ZERO);
                }
                buf.writeBytes((sb.toString() + lenStr).getBytes());
                break;
            //左边补空格
            case LEFT_BLANK:
                for (int i = 0; i < subInt; i++) {
                    sb.append(StringUtils.SPACE);
                }
                buf.writeBytes((sb.toString() + lenStr).getBytes());
                break;
            //右边补空格
            case RIGHT_BLANK:
                for (int i = 0; i < subInt; i++) {
                    sb.append(StringUtils.SPACE);
                }
                buf.writeBytes((lenStr + sb.toString()).getBytes());
                break;
            default:
        }

    }

    private static void writeByteLen(ByteBuf buf, int totalLen, int lengthZoneLength) {
        switch (lengthZoneLength) {
            case 1:
                buf.writeByte(totalLen);
                break;
            case 2:
                buf.writeShort(totalLen);
                break;
            case 3:
                buf.writeMedium(totalLen);
                break;
            case 4:
                buf.writeInt(totalLen);
                break;
            case 8:
                buf.writeLong(totalLen);
                break;
            default:
                throw new EncoderException(
                        "不支持的长度域长度: " + lengthZoneLength + " (期望值: 1, 2, 3, 4, or 8)");
        }
    }

    private static int getLen(DataPacketModel.LengthMode lengthMode, int dataLength, int placeholderZoneLength, int lengthZoneLength) {
        switch (lengthMode) {
            case DATA:
                return dataLength;
            case LENGTH_DATA:
                return lengthZoneLength + dataLength;
            case LENGTH_PLACEHOLDER:
                return lengthZoneLength + placeholderZoneLength;
            case PLACEHOLDER_DATA:
                return dataLength + placeholderZoneLength;
            case TOTAL:
                return dataLength + placeholderZoneLength + lengthZoneLength;
            default:
        }
        return 0;
    }
}
