package com.silence.hrpc.service;

import com.silence.service.impl.SmsServiceImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcService {
    static class ServiceTask implements Runnable {
        private Socket socket;
        private int port;

        public ServiceTask(Socket socket,int port) {
            this.socket = socket;
            this.port = port;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

                String serviceName = inputStream.readUTF();
                String methodName = inputStream.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                Object[] args = (Object[]) inputStream.readObject();

                Class<?> serviceClass = Class.forName(serviceName);
                Method declaredMethod = serviceClass.getDeclaredMethod(methodName, parameterTypes);
                //todo 此处应该改成根绝serviceName获得 实现类的实例 后期和spring集成
                Object result = declaredMethod.invoke(SmsServiceImpl.class.getConstructor().newInstance(), args);
                System.out.println("服务端响应成功。服务端口是："+port);
                outputStream.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void startService(String serviceName, String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("服务在端口:" + port + "启动了。");
            RegisterServiceCenter.register2Center(serviceName,host,port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServiceTask(socket,port)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
