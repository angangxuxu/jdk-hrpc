package com.silence.rpc.client;

import com.silence.hrpc.ServiceAction;
import com.silence.service.ServiceWrap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceCenter {
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static ServiceWrap getService(String serviceName)
            throws Exception {
        Set<ServiceWrap> serviceWraps = getServiceList(serviceName, "127.0.0.1", 5001);
        int serviceIndex = random.nextInt(serviceWraps.size());
        System.out.println(serviceWraps.size()+":"+serviceIndex);
        int i = 0;
        ServiceWrap serviceWrap = null;
        for (ServiceWrap wrap : serviceWraps) {
            if (serviceIndex == i++){
                serviceWrap = wrap;
            }
        }
        System.out.println("本次选择了服务器：" + serviceWrap);
        return serviceWrap;
    }

    private static Set<ServiceWrap> getServiceList(String serviceName, String host, int port)
            throws Exception {

        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(ServiceAction.OBTAIN_SERVICE);
            outputStream.writeUTF(serviceName);
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());
            Set<ServiceWrap> services = (Set<ServiceWrap>) inputStream.readObject();

            System.out.println("获得服务[" + serviceName + "]提供者的地址列表[" + services + "]，准备调用。");
            return services;
        } finally {
            if (socket != null) socket.close();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
        }

    }
}
