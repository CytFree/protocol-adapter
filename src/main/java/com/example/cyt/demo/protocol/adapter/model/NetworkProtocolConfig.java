package com.example.cyt.demo.protocol.adapter.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author by CYT
 * @Version 1.0
 * @Description 通讯协议参数配置
 * @Date 2019-07-15 15:14
 */
@Data
@ToString
@Builder
public class NetworkProtocolConfig implements Serializable {
    /**
     * 通信协议类型
     *
     * @see com.example.cyt.demo.protocol.adapter.enums.ProtocolTypeEnum
     */
    private String protocolType;

    /**
     * 端口号（一般为1024~65535）
     */
    private Integer port;

    /**
     * 最大连接数，并发数
     */
    private Integer maxActive;

    /**
     * 超时时间
     */
    private Long timeOut;

    /**
     * ip，接出端服务ip
     */
    private String ip;

    /**
     * 服务路径, 通讯协议为HTTP或HTTPS时有效
     */
    private String path;
}
