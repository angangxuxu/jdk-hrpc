package com.silence.service.impl;


import com.silence.service.SmsService;

public class SmsServiceImpl implements SmsService {

    @Override
    public String sendSms(String user) {
        return "成功发送短信给" + user;
    }
}
