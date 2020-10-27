package com.silence.rpc.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcClient {

    public static <T> T getRpcProxy(String host, int port, Class<?> serviceClass){
        InetSocketAddress address = new InetSocketAddress(host, port);
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new InvokeProcess(address, serviceClass.getName()));
    }

    static class InvokeProcess implements InvocationHandler {

        private InetSocketAddress address;
        private String serviceName;

        public InvokeProcess(InetSocketAddress address, String serviceName) {
            this.address = address;
            this.serviceName = serviceName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Socket socket = null;
            ObjectOutputStream outputStream = null;
            ObjectInputStream inputStream = null;
            try {
                socket = new Socket();
                socket.connect(address);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream.writeUTF(serviceName);
                outputStream.writeUTF(method.getName());
                outputStream.writeObject(method.getParameterTypes());
                outputStream.writeObject(args);
                outputStream.flush();
                Object result = inputStream.readObject();
                System.out.println(serviceName + " remote execute success!");
                return result;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
