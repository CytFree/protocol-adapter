package com.hsjry.packet.channel.util;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-16 11:21
 */
public class LengthFieldBasedFrameDecoderUtil {
    /**
     * 根据长度解决半包，黏包问题
     *
     * @return
     */
    public static LengthFieldBasedFrameDecoder createLengthFieldBasedFrameDecoder(MessageDataStructConfig dataStructConfig) {
        //报文结构模式
        DataPacketModel.StructMode structMode;
        Integer boundSide = dataStructConfig.getBoundSide();
        if (boundSide == 0) {
            structMode = dataStructConfig.getRecvStructMode();
        } else {
            structMode = dataStructConfig.getSendStructMode();
        }
        if (structMode == null) {
            throw new RuntimeException("报文数据结构为空");
        }
        if (DataPacketModel.StructMode.DATA.equals(structMode) || DataPacketModel.StructMode.PLACEHOLDER_DATA.equals(structMode)) {
            //只有数据，无长度域，直接返回空
            //占位符 + 数据，无长度域，直接返回空
            return null;
        } else if (DataPacketModel.StructMode.LENGTH_DATA.equals(structMode)) {
            //长度 + 数据
            return lengthDataDecoder(dataStructConfig, structMode, boundSide);
        } else if (DataPacketModel.StructMode.LENGTH_PLACEHOLDER_DATA.equals(structMode)) {
            //长度 + 占位符 + 数据
            return lengthPlaceHolderDataDecoder(dataStructConfig, structMode, boundSide);
        } else if (DataPacketModel.StructMode.PLACEHOLDER_LENGTH_DATA.equals(structMode)) {
            //占位符 + 长度 + 数据
            return placeHolderLengthDataDecoder(dataStructConfig, structMode, boundSide);
        } else {
            throw new RuntimeException("不支持的报文数据结构：" + structMode);
        }
    }

    private static LengthFieldBasedFrameDecoder lengthDataDecoder(
            MessageDataStructConfig dataStructConfig, DataPacketModel.StructMode structMode, Integer boundSide) {
        Integer maxFrameLength = Integer.MAX_VALUE, lengthFieldOffset = 0, lengthFieldLength, lengthAdjustment = 0, initialBytesToStrip;
        DataPacketModel.LengthMode lengthMode;
        if (boundSide == 0) {
            lengthFieldLength = dataStructConfig.getRecvLengthZoneLength();
            lengthMode = dataStructConfig.getRecvLengthMode();
        } else {
            lengthFieldLength = dataStructConfig.getSendLengthZoneLength();
            lengthMode = dataStructConfig.getSendLengthMode();
        }
        switch (lengthMode) {
            case TOTAL:
                //长度字段的长度 + 数据的长度
                lengthAdjustment = -lengthFieldLength;
                break;
            case DATA:
                //数据的长度
                break;
            case LENGTH_DATA:
                //长度字段的长度 + 数据的长度
                lengthAdjustment = -lengthFieldLength;
                break;
            default:
                throw new RuntimeException("数据结构[" + structMode + "]不支持该数据长度域模式：" + lengthMode);
        }
        initialBytesToStrip = lengthFieldLength;
        return new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    private static LengthFieldBasedFrameDecoder lengthPlaceHolderDataDecoder(
            MessageDataStructConfig dataStructConfig, DataPacketModel.StructMode structMode, Integer boundSide) {
        Integer maxFrameLength = Integer.MAX_VALUE, lengthFieldOffset = 0, lengthFieldLength, lengthAdjustment = 0, initialBytesToStrip;
        Integer placeHolderLen;
        DataPacketModel.LengthMode lengthMode;
        if (boundSide == 0) {
            placeHolderLen = dataStructConfig.getRecvPlaceholder().length();
            lengthFieldLength = dataStructConfig.getRecvLengthZoneLength();
            lengthMode = dataStructConfig.getRecvLengthMode();
        } else {
            placeHolderLen = dataStructConfig.getSendPlaceholder().length();
            lengthFieldLength = dataStructConfig.getSendLengthZoneLength();
            lengthMode = dataStructConfig.getSendLengthMode();
        }
        switch (lengthMode) {
            case TOTAL:
                //长度字段的长度 + 占位符的长度 + 数据的长度
                lengthAdjustment = -lengthFieldLength;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case DATA:
                //数据的长度
                lengthAdjustment = placeHolderLen;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case LENGTH_DATA:
                //长度字段的长度 + 数据的长度
                lengthAdjustment = placeHolderLen - lengthFieldLength;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case PLACEHOLDER_DATA:
                //占位符的长度 + 数据的长度
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            default:
                throw new RuntimeException("数据结构[" + structMode + "]不支持该数据长度域模式：" + lengthMode);
        }
        return new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    private static LengthFieldBasedFrameDecoder placeHolderLengthDataDecoder(
            MessageDataStructConfig dataStructConfig, DataPacketModel.StructMode structMode, Integer boundSide) {
        Integer maxFrameLength = Integer.MAX_VALUE, lengthFieldOffset, lengthFieldLength, lengthAdjustment = 0, initialBytesToStrip;
        Integer placeHolderLen;
        DataPacketModel.LengthMode lengthMode;
        if (boundSide == 0) {
            lengthMode = dataStructConfig.getRecvLengthMode();
            placeHolderLen = dataStructConfig.getRecvPlaceholder().length();
            lengthFieldLength = dataStructConfig.getRecvLengthZoneLength();
        } else {
            lengthMode = dataStructConfig.getSendLengthMode();
            placeHolderLen = dataStructConfig.getSendPlaceholder().length();
            lengthFieldLength = dataStructConfig.getSendLengthZoneLength();
        }
        switch (lengthMode) {
            case TOTAL:
                //占位符的长度 + 长度字段的长度 + 数据的长度
                lengthFieldOffset = placeHolderLen;
                lengthAdjustment = -placeHolderLen - lengthFieldLength;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case DATA:
                //数据的长度
                lengthFieldOffset = placeHolderLen;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case LENGTH_DATA:
                //长度字段的长度 + 数据的长度
                lengthFieldOffset = placeHolderLen;
                lengthAdjustment = -lengthFieldLength;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            case PLACEHOLDER_DATA:
                //占位符的长度 + 数据的长度
                lengthFieldOffset = placeHolderLen;
                lengthAdjustment = -placeHolderLen;
                initialBytesToStrip = lengthFieldLength + placeHolderLen;
                break;
            default:
                throw new RuntimeException("数据结构[" + structMode + "]不支持该数据长度域模式：" + lengthMode);
        }
        return new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
