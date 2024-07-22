package com.example.myapplication.entity;

public class HeaderEntity {
    private int messageLength;
    private MessageTypeEnum messageType;
    private String version;
    private String checksum;

    public HeaderEntity(int messageLength, MessageTypeEnum messageType, String version, String checksum) {
        this.messageLength = messageLength;
        this.messageType = messageType;
        this.version = version;
        this.checksum = checksum;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public MessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "HeaderEntity{" +
                "messageLength=" + messageLength +
                ", messageType=" + messageType +
                ", version='" + version + '\'' +
                ", checksum='" + checksum + '\'' +
                '}';
    }
}
