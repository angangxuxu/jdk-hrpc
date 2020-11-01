package com.silence;

import com.silence.hrpc.service.RpcService;

import java.util.concurrent.ThreadLocalRandom;

public class ServiceMain {
    public static void main(String[] args) {

        new Thread(() -> {
            RpcService.startService("sms-service", "127.0.0.1", 5002);
        }).start();
        new Thread(() -> {
            RpcService.startService("sms-service", "127.0.0.1", 5003);
        }).start();
    }
}
