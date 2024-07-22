package com.example.myapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseGetRandomEntity implements MessageData{
    private String status;
    private int randomCount;
    private String random;

    @JsonCreator
    public ResponseGetRandomEntity(
            @JsonProperty("status") String status,
            @JsonProperty("random") int randomCount,
            @JsonProperty("random")String random) {
        this.status = status;
        this.randomCount = randomCount;
        this.random = random;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRandomCount() {
        return randomCount;
    }

    public void setRandomCount(int randomCount) {
        this.randomCount = randomCount;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }


}
