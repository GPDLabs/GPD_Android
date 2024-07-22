package com.example.myapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageEntity {
    private HeaderEntity header;
    private MessageBodyEntity body;

    @JsonCreator
    public MessageEntity(@JsonProperty("header") HeaderEntity header, @JsonProperty("body") MessageBodyEntity body) {
        this.header = header;
        this.body = body;
    }

    public HeaderEntity getHeader() {
        return header;
    }

    public void setHeader(HeaderEntity header) {
        this.header = header;
    }

    public MessageBodyEntity getBody() {
        return body;
    }

    public void setBody(MessageBodyEntity body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
