package com.silence.rpc.client;

import com.silence.service.ServiceWrap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcClient {

    public static <T> T getRpcProxy(String serviceName,Class<?> serviceClass) throws Exception {
        ServiceWrap serviceWrap = ServiceCenter.getService(serviceName);
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new InvokeProcess(serviceWrap, serviceClass.getName()));
    }

    static class InvokeProcess implements InvocationHandler {

        private ServiceWrap serviceWrap;
        private String serviceName;

        public InvokeProcess(ServiceWrap serviceWrap, String serviceName) {
            this.serviceWrap = serviceWrap;
            this.serviceName = serviceName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Socket socket = null;
            ObjectOutputStream outputStream = null;
            ObjectInputStream inputStream = null;
            try {
                socket = new Socket();
                InetSocketAddress address = new InetSocketAddress(serviceWrap.getHost(),serviceWrap.getPort());
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
