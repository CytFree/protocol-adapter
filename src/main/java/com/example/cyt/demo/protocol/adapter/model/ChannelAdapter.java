package com.example.cyt.demo.protocol.adapter.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author by CYT
 * @Version 1.0
 * @Description 渠道适配器
 * @Date 2019-07-12 13:47
 */
@Data
@Builder
@ToString
public class ChannelAdapter {
    /**
     * 适配器编号
     */
    private String adapterCode;

    /**
     * 适配器名称
     */
    private String adapterName;

    /**
     * 渠道适配器请求接入配置
     */
    private ChannelAdapterReqConfig adapterReqConfig;

    /**
     * 渠道适配器响应接出配置
     */
    private ChannelAdapterRespConfig adapterRespConfig;


}
