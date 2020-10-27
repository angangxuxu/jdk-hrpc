package com.silence;

import com.silence.rpc.client.RpcClient;
import com.silence.service.SmsService;

public class ClientMain {
    public static void main(String[] args) {
        try {
            SmsService smsService = RpcClient.getRpcProxy("127.0.0.1", 50001, SmsService.class);
            String result = smsService.sendSms("徐传超");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
