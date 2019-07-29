package com.hsjry.packet.channel.util;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.model.MessageDataStructConfig;
import io.netty.buffer.ByteBuf;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-29 17:03
 */
public class HttpContentUtil {
    public static void getBodyFromHttpContext(ByteBuf buf, MessageDataStructConfig dataStructConfig) {
        //数据结构
        Integer boundSide = dataStructConfig.getBoundSide();
        DataPacketModel.StructMode structMode;
        //数据长度域的长度
        Integer lengthZoneLength;
        //占位符
        String placeHolder;
        if (boundSide == 0) {
            //接入请求
            structMode = dataStructConfig.getRecvStructMode();
            lengthZoneLength = dataStructConfig.getRecvLengthZoneLength();
            placeHolder = dataStructConfig.getRecvPlaceholder();
        } else {
            //接出响应
            structMode = dataStructConfig.getSendStructMode();
            lengthZoneLength = dataStructConfig.getSendLengthZoneLength();
            placeHolder = dataStructConfig.getSendPlaceholder();
        }

        switch (structMode) {
            case DATA:
                break;
            case LENGTH_DATA:
                //长度 + 数据
                //长度域长度
                byte[] lengthBody = new byte[lengthZoneLength];
                buf.readBytes(lengthBody, 0, lengthBody.length);
                break;
            case LENGTH_PLACEHOLDER_DATA:
                //占位符的长度
                Integer placeHolderZoneLength = placeHolder.length();
                lengthBody = new byte[lengthZoneLength + placeHolderZoneLength];
                buf.readBytes(lengthBody, 0, lengthBody.length);
                break;
            case PLACEHOLDER_DATA:
                //占位符 + 数据
                //占位符长度
                placeHolderZoneLength = placeHolder.length();
                lengthBody = new byte[placeHolderZoneLength];
                buf.readBytes(lengthBody, 0, lengthBody.length);
                break;
            case PLACEHOLDER_LENGTH_DATA:
                placeHolderZoneLength = placeHolder.length();
                lengthBody = new byte[lengthZoneLength + placeHolderZoneLength];
                buf.readBytes(lengthBody, 0, lengthBody.length);
                break;
            default:
        }
    }
}
