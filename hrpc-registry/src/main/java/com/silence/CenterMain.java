package com.silence;

import com.silence.hrpc.RegistrationCenter;

import java.io.IOException;
import java.util.Random;

public class CenterMain {
    public static void main(String[] args) {
        try {
            RegistrationCenter.startRegistrationCenter(5001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
