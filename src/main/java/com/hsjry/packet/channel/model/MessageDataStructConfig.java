package com.hsjry.packet.channel.model;

import com.hsjry.packet.adaption.model.DataPacketModel;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author by CYT
 * @Version 1.0
 * @Description 报文数据结构配置
 * @Date 2019-07-15 14:39
 */
@Data
@ToString
@Builder
public class MessageDataStructConfig implements Serializable {
    /**
     * 接入接出类型，0-接入；1-接出
     */
    private Integer boundSide;

    /**
     * 报文结构模式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.StructMode
     */
    private DataPacketModel.StructMode recvStructMode;
    /**
     * 长度模式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.LengthMode
     */
    private DataPacketModel.LengthMode recvLengthMode;
    /**
     * 长度域长度
     */
    private Integer recvLengthZoneLength;
    /**
     * 占位字符串
     */
    private String recvPlaceholder;
    /**
     * 长度域补齐方式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.LengthZoneFillMode
     */
    private DataPacketModel.LengthZoneFillMode recvLengthZoneFillMode;
    /**
     * 插件
     */
    private String recvPlugin;

    /**
     * 报文结构模式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.StructMode
     */
    private DataPacketModel.StructMode sendStructMode;
    /**
     * 长度模式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.LengthMode
     */
    private DataPacketModel.LengthMode sendLengthMode;
    /**
     * 长度域长度
     */
    private Integer sendLengthZoneLength;
    /**
     * 占位字符串
     */
    private String sendPlaceholder;
    /**
     * 长度域补齐方式
     *
     * @see com.hsjry.packet.adaption.model.DataPacketModel.LengthZoneFillMode
     */
    private DataPacketModel.LengthZoneFillMode sendLengthZoneFillMode;
    /**
     * 插件
     */
    private String sendPlugin;
}
