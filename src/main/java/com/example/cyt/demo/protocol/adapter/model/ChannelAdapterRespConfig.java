package com.example.cyt.demo.protocol.adapter.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author by CYT
 * @Version 1.0
 * @Description 渠道适配器，响应接出配置
 * @Date 2019-07-12 14:03
 */
@Data
@ToString
@Builder
public class ChannelAdapterRespConfig implements Serializable {
    /**
     * 通讯协议配置
     */
    private NetworkProtocolConfig networkProtocolConfig;

    /**
     * 报文数据结构
     */
    private MessageDataStructConfig messageDataStructConfig;

    /**
     * 报文类型
     * @see com.hsjry.packet.adaption.enums.EnumPacketType
     */
    private Integer packetType;

    /**
     * 报文字符编码格式
     */
    private String charsetEncoding;

    /**
     * 交易代码赋值方式
     */
    private Integer tradeCodeWay;

    /**
     * 报文内取交易码，交易代码赋值字典
     */
    private String tradeCodePath;

    /**
     * 交易代码赋值字典
     */
    private String dicMappingCode;
}
