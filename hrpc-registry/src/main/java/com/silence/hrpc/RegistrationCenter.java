package com.silence.hrpc;

import com.silence.service.ServiceWrap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationCenter {
    private static final Map<String, Set<ServiceWrap>> serviceHolder = new ConcurrentHashMap<>();

    public static void registerService(String serviceName, String host, int port) {

        serviceHolder.compute(serviceName, (k, v) -> {
            if (v == null) {
                v = new HashSet<>();

            }
            ServiceWrap service = new ServiceWrap(host, port);
            v.add(service);
            return v;
        });
    }

    public static Set<ServiceWrap> getService(String serviceName) {
        return serviceHolder.get(serviceName);
    }

    static class ServiceTask<T> implements Runnable {
        private Socket socket;

        public ServiceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                ServiceAction serviceType = (ServiceAction)inputStream.readObject();
                if(serviceType == ServiceAction.OBTAIN_SERVICE){
                    String serviceName = inputStream.readUTF();
                    Set<ServiceWrap> result = getService(serviceName);
                    outputStream.writeObject(result);
                    System.out.println("将已注册的服务["+serviceName+"]提供给客户端");
                }else if(serviceType == ServiceAction.REGISTER_SERVICE){
                    String serviceName = inputStream.readUTF();
                    String host = inputStream.readUTF();
                    int port = inputStream.readInt();
                    registerService(serviceName,host,port);
                    outputStream.writeBoolean(true);
                    //outputStream.flush();
                    System.out.println("已将服务["+serviceName+"]注册到注册中心");
                }

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
            }
        }
    }



    public static void  startRegistrationCenter(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        System.out.println("服务注册中心 在端口:"+port+":运行");
        try{
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServiceTask(socket)).start();
            }
        }finally {
            serverSocket.close();
        }
    }
}
