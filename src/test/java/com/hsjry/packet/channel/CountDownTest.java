package com.hsjry.packet.channel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author by CYT
 * @Version 1.0
 * @Description TODO
 * @Date 2019-07-26 17:01
 */
public class CountDownTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(1);
        downLatch.await(100, TimeUnit.MILLISECONDS);
    }
}
