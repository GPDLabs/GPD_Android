package com.example.myapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseEntity {
    private HeaderEntity header;
    private ResponseBodyEntity body;

    @JsonCreator
    public ResponseEntity(@JsonProperty("header") HeaderEntity header, @JsonProperty("body") ResponseBodyEntity body) {
        this.header = header;
        this.body = body;
    }

    public HeaderEntity getHeader() {
        return header;
    }

    public void setHeader(HeaderEntity header) {
        this.header = header;
    }

    public ResponseBodyEntity getBody() {
        return body;
    }

    public void setBody(ResponseBodyEntity body) {
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
