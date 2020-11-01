package com.silence.service;

import java.io.Serializable;

public class ServiceWrap implements Serializable {

    private static final long serialVersionUID = 7268736010305220037L;
    private String host;
    private int port;

    public ServiceWrap(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceWrap service = (ServiceWrap) o;

        if (port != service.port) return false;
        return host.equals(service.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "ServiceWrap{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
