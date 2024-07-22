package com.example.myapplication.entity;

public class WirelessConfEntity implements MessageData{
    private String wifiName;
    private String wifiPwd;

    public WirelessConfEntity(String wifiName, String wifiPwd) {
        this.wifiName = wifiName;
        this.wifiPwd = wifiPwd;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    @Override
    public String toString() {
        return "WirelessConfEntity{" +
                "wifiName='" + wifiName + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                '}';
    }
}
