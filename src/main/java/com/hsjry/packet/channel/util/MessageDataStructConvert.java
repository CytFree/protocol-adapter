package com.hsjry.packet.channel.util;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;

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
                    writeLen(buf, totalLen, lengthZoneLength);
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                case LENGTH_PLACEHOLDER_DATA:
                    totalLen = getLen(lengthMode, dataLength, placeHolder.length(), lengthZoneLength);
                    buf = Unpooled.buffer(lengthZoneLength + dataLength + placeHolder.length());
                    writeLen(buf, totalLen, lengthZoneLength);
                    buf.writeBytes(placeHolder.getBytes(charsetName));
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                case PLACEHOLDER_DATA:
                    return Unpooled.wrappedBuffer((placeHolder + body).getBytes(charsetName));
                case PLACEHOLDER_LENGTH_DATA:
                    totalLen = getLen(lengthMode, dataLength, placeHolder.length(), lengthZoneLength);
                    buf = Unpooled.buffer(lengthZoneLength + dataLength + placeHolder.length());
                    buf.writeBytes(placeHolder.getBytes(charsetName));
                    writeLen(buf, totalLen, lengthZoneLength);
                    buf.writeBytes(body.getBytes(charsetName));
                    return buf;
                default:
            }
        }
        return Unpooled.wrappedBuffer(body.getBytes(charsetName));
    }

    private static void writeLen(ByteBuf buf, int totalLen, int lengthZoneLength) {
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
                throw new DecoderException(
                        "unsupported lengthFieldLength: " + lengthZoneLength + " (expected: 1, 2, 3, 4, or 8)");
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
