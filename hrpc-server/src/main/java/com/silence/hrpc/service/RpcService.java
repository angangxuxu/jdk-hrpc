package com.silence.hrpc.service;

import com.silence.service.impl.SmsServiceImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcService {
    static class ServiceTask<T> implements Runnable {
        private Socket socket;

        public ServiceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

                String serviceName = inputStream.readUTF();
                String methodName = inputStream.readUTF();
                Class<T>[] parameterTypes = (Class<T>[]) inputStream.readObject();
                Object[] args = (Object[]) inputStream.readObject();

                Class<?> serviceClass = Class.forName(serviceName);
                Method declaredMethod = serviceClass.getDeclaredMethod(methodName, parameterTypes);
                //todo 此处应该改成根绝serviceName获得 实现类的实例 后期和spring集成
                Object result = declaredMethod.invoke(SmsServiceImpl.class.getConstructor().newInstance(), args);
                System.out.println("服务端响应成功");
                outputStream.writeObject(result);
            } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
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

    public static void startService(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("RPC service on:" + port + " started");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServiceTask(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
