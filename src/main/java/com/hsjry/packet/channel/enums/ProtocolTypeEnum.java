package com.hsjry.packet.channel.enums;

/**
 * @author by CYT
 * @Version 1.0
 * @Description 通讯协议类型
 * @Date 2019-07-12 13:48
 */
public enum ProtocolTypeEnum {
    /**
     * HTTP协议
     */
    HTTP("http", "HTTP协议"),
    /**
     * HTTP协议 + SSL/TLS
     */
    HTTPS("https", "HTTP协议"),
    /**
     * TCP协议
     */
    TCP("tcp", "HTTP协议"),
    /**
     * TCP协议 + SSL/TLS
     */
    TCPS("tcps", "HTTP协议");

    private final String code;
    private final String msg;

    ProtocolTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ProtocolTypeEnum getByCode(String code) {
        for (ProtocolTypeEnum protocolType : ProtocolTypeEnum.values()) {
            if (protocolType.getCode().equals(code)) {
                return protocolType;
            }
        }
        return null;
    }
}
