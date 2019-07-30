package com.hsjry.packet.channel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@RunWith(JUnit4.class)
public class TcpReqClient {
    @Test
    public void testString() {
        String sss = "  0001234567890  ";
        sss = sss.replaceAll(" ", "");
        int lastIndex = 0;
        for (int i = 0; i < sss.length(); i++) {
            char c = sss.charAt(i);
            if (c != '0') {
                lastIndex = i;
                break;
            }
        }
        sss = sss.substring(lastIndex);
        System.out.println(Integer.parseInt(sss));
        byte[] bytes = sss.getBytes();
        System.out.println(bytes.length);
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i <= 3; i++) {
            test();
        }
    }

    public static void test() throws IOException {
        int port = 8030;
        Socket socket = new Socket("127.0.0.1", port);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        byte[] body = "cyt你好a，好，中国你as哈哈a，好，中国你好啊你好as哈哈你好a，好，s哈哈你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈saaaa呵aaa呵".getBytes();
        outputStream.write("000000".getBytes());
        int lengthZoneLen = body.length + 6;
//        outputStream.writeInt(lengthZoneLen);
        StringBuffer stringBuffer = new StringBuffer();
        String str = String.valueOf(lengthZoneLen);
        stringBuffer.append(str);
        if (str.length() < 4) {
            int sub = 4 - str.length();
            for (int i=0;i<sub;i++) {
                stringBuffer.append(" ");
            }
        }
        outputStream.write(stringBuffer.toString().getBytes());
        outputStream.write(body);
        //关闭输出流，不再发送消息（如果再需要发送消息，就需要再重新socket）
        socket.shutdownOutput();

        long s = System.currentTimeMillis();
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, len, "UTF-8"));
        }
        long end = System.currentTimeMillis();
        System.out.println("收到服务端回应：" + sb.toString());
        System.out.println("耗时：" + (end - s) + "ms");
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
