package com.example.myapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseWalletAddrEntity implements MessageData{
    private String status;
    private String walletAddr;
    private String pubKey;

    @JsonCreator
    public ResponseWalletAddrEntity(
            @JsonProperty("status") String status,
            @JsonProperty("walletAddr") String walletAddr,
            @JsonProperty("pubKey") String pubKey
    ) {
        this.status = status;
        this.walletAddr = walletAddr;
        this.pubKey = pubKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWalletAddr() {
        return walletAddr;
    }

    public void setWalletAddr(String walletAddr) {
        this.walletAddr = walletAddr;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
}
