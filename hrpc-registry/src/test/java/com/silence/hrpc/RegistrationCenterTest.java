package com.silence.hrpc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationCenterTest {

  /*  @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }*/

    @Test
    @DisplayName("注册中心")
    void registerService() {
        RegistrationCenter registrationCenter = new RegistrationCenter();
        registrationCenter.registerService("mall-service","192.168.25.1",8001);
        System.out.println(RegistrationCenter.getService("mall-service"));
    }

    @Test
    @DisplayName("测试方法")
    void test(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(serverSocket.getInetAddress().getHostAddress());
    }
}