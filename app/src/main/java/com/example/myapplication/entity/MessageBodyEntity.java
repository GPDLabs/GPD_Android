package com.example.myapplication.entity;

public class MessageBodyEntity {
    private MessageData messageData;
    private String messageType;


    public MessageBodyEntity(String messageType, MessageData messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        messageType = messageType;
    }

    public com.example.myapplication.entity.MessageData getMessageData() {
        return messageData;
    }

    public void setMessageData(com.example.myapplication.entity.MessageData messageData) {
        this.messageData = messageData;
    }

    @Override
    public String toString() {
        return "MessageBodyEntity{" +
                "MessageType='" + messageType + '\'' +
                ", MessageData=" + messageData +
                '}';
    }
}
