package com.silence.hrpc.service;

import com.silence.hrpc.ServiceAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RegisterServiceCenter {

    public static void register2Center(String serviceName, String host, int port) throws IOException {
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 5001));
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(ServiceAction.REGISTER_SERVICE);
            output.writeUTF(serviceName);
            output.writeUTF(host);
            output.writeInt(port);
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            if (input.readBoolean()) {
                System.out.println("服务[" + serviceName + "]注册成功!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
            if (output != null) output.close();
            if (input != null) input.close();
        }
    }
}
