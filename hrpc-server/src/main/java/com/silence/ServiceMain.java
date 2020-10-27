package com.silence;

import com.silence.hrpc.service.RpcService;

public class ServiceMain {
    public static void main(String[] args) {
        RpcService.startService(50001);
    }
}
