package com.example.myapplication.entity;

public class ConnectVqrEntity implements MessageData{
    private String ipAddr;
    private int keyNo;

    public ConnectVqrEntity(String ipAddr, int keyNo) {
        this.ipAddr = ipAddr;
        this.keyNo = keyNo;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getKeyNo() {
        return keyNo;
    }

    public void setKeyNo(int keyNo) {
        this.keyNo = keyNo;
    }

    @Override
    public String toString() {
        return "ConnectVqrEntity{" +
                "ipAddr='" + ipAddr + '\'' +
                ", keyNo=" + keyNo +
                '}';
    }
}
