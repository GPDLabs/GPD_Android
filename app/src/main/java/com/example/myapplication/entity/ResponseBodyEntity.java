package com.example.myapplication.entity;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

public class ResponseBodyEntity {
    private String messageData;
    private String messageType;

    @JsonCreator
    public ResponseBodyEntity(
            @JsonProperty("messageData") String messageData,
            @JsonProperty("messageType") String messageType
    ) {
        this.messageData = messageData;
        this.messageType = messageType;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        messageType = messageType;
    }

    @Override
    public String toString() {
        return "ResponseBodyEntity{" +
                "MessageData=" + messageData +
                ", MessageType='" + messageType + '\'' +
                '}';
    }
}
