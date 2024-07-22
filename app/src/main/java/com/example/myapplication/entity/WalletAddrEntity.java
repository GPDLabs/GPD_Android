package com.example.myapplication.entity;

public class WalletAddrEntity implements MessageData{
    private int KeyNo;

    public WalletAddrEntity(int keyNo) {
        KeyNo = keyNo;
    }

    public int getKeyNo() {
        return KeyNo;
    }

    public void setKeyNo(int keyNo) {
        KeyNo = keyNo;
    }

    @Override
    public String toString() {
        return "WalletAddrEntity{" +
                "KeyNo=" + KeyNo +
                '}';
    }
}
