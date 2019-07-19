package com.example.cyt.demo.protocol.adapter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpReqClient {
    public static void main(String[] args) throws IOException {
       for (int i = 0; i <= 1; i++) {
           test();
       }
    }

    public static void test () throws IOException {
        int port = 8020;
        Socket socket = new Socket("127.0.0.1", port);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        byte[] body = "你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈你好a，好你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈你好a，好，中国你好啊你好as哈哈saaaa呵呵".getBytes();
//        outputStream.write("000000".getBytes());
//        outputStream.writeInt(body.length + 6);
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
