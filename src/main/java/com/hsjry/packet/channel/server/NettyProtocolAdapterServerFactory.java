package com.hsjry.packet.channel.server;

import com.hsjry.packet.adaption.model.DataPacketModel;
import com.hsjry.packet.channel.enums.ProtocolTypeEnum;
import com.hsjry.packet.channel.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-12 14:18
 */
public class NettyProtocolAdapterServerFactory {

    public List<NettyProtocolAdapterServer> createAllServer() {
        List<NettyProtocolAdapterServer> adapterServerList = new ArrayList<>();
        ChannelAdapterConfig channelAdapter1 =
                createTestChannelAdapter(8020, 9002,
                        ProtocolTypeEnum.TCP.getCode(), ProtocolTypeEnum.HTTP.getCode(), "appAdapter001");
        ChannelAdapterConfig channelAdapter2 =
                createTestChannelAdapter(8030, 9012,
                        ProtocolTypeEnum.TCP.getCode(), ProtocolTypeEnum.TCP.getCode(), "appAdapter002");

        NettyProtocolAdapterServer nettyServer1 = createServer(channelAdapter1);
        NettyProtocolAdapterServer nettyServer2 = createServer(channelAdapter2);
        if (nettyServer1 != null) {
            adapterServerList.add(nettyServer1);
        }
        if (nettyServer2 != null) {
            adapterServerList.add(nettyServer2);
        }
        return adapterServerList;
    }

    public static NettyProtocolAdapterServer createServer(ChannelAdapterConfig channelAdapter) {
        NettyProtocolAdapterServer nettyServer = null;
        ProtocolTypeEnum protocolType =
                ProtocolTypeEnum.getByCode(channelAdapter.getAdapterReqConfig().getNetworkProtocolConfig().getProtocolType());
        if (protocolType != null) {
            switch (protocolType) {
                case TCP:
                    nettyServer = new TcpNettyProtocolAdapterServer(channelAdapter);
                    break;
                case HTTP:
                    nettyServer = new HttpNettyProtocolAdapterServer(channelAdapter);
                    break;
                case HTTPS:
                    break;
                case TCPS:
                    break;
                default:
            }
        }
        return nettyServer;
    }

    /**
     * TODO 创建测试数据
     *
     * @return
     */
    private static ChannelAdapterConfig createTestChannelAdapter(int reqPort, int respPort,
                                                                 String reqProtocolType, String respProtocolType, String adapterCode) {
        NetworkProtocolConfig reqNetworkProtocolConfig = NetworkProtocolConfig.builder()
                .protocolType(reqProtocolType)
                .port(reqPort)
                .build();

        MessageDataStructConfig reqDataStructConfig = MessageDataStructConfig.builder()
//                .recvLengthMode(DataPacketModel.LengthMode.PLACEHOLDER_DATA)
//                .recvLengthZoneLength(4)
//                .recvPlaceholder("000000")
//                .recvStructMode(DataPacketModel.StructMode.PLACEHOLDER_LENGTH_DATA)
                .recvStructMode(DataPacketModel.StructMode.DATA)

                .sendStructMode(DataPacketModel.StructMode.LENGTH_PLACEHOLDER_DATA)
                .sendPlaceholder("BBBBB")
                .sendLengthMode(DataPacketModel.LengthMode.TOTAL)
                .sendLengthZoneLength(4)
                .boundSide(0)
                .build();

        ChannelAdapterReqConfig reqConfig = ChannelAdapterReqConfig.builder()
                .charsetEncoding("UTF-8")
                .networkProtocolConfig(reqNetworkProtocolConfig)
                .messageDataStructConfig(reqDataStructConfig)
                .build();

        NetworkProtocolConfig networkProtocolConfig = NetworkProtocolConfig.builder()
                .protocolType(respProtocolType)
                .ip("127.0.0.1")
                .port(respPort)
                .path("/packet")
                .build();

        MessageDataStructConfig respDataStructConfig = MessageDataStructConfig.builder()
                .sendLengthMode(DataPacketModel.LengthMode.DATA)
                .sendLengthZoneLength(4)
                .sendPlaceholder("000000")
                .sendStructMode(DataPacketModel.StructMode.LENGTH_DATA)

                .recvStructMode(DataPacketModel.StructMode.LENGTH_PLACEHOLDER_DATA)
                .recvPlaceholder("AAAAA")
                .recvLengthMode(DataPacketModel.LengthMode.TOTAL)
                .recvLengthZoneLength(4)
                .boundSide(1)
                .build();

        ChannelAdapterRespConfig respConfig = ChannelAdapterRespConfig.builder()
                .charsetEncoding("UTF-8")
                .networkProtocolConfig(networkProtocolConfig)
                .messageDataStructConfig(respDataStructConfig)
                .build();

        ChannelAdapterConfig channelAdapter = ChannelAdapterConfig.builder()
                .adapterCode(adapterCode)
                .adapterName("APP渠道接入适配器")
                .adapterReqConfig(reqConfig)
                .adapterRespConfig(respConfig)
                .build();
        return channelAdapter;
    }
}
