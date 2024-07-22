package com.example.myapplication.entity;

public class GetRandomEntity implements MessageData{
    private int randomCount;

    public GetRandomEntity(int randomCount) {
        this.randomCount = randomCount;
    }

    public int getRandomCount() {
        return randomCount;
    }

    public void setRandomCount(int randomCount) {
        this.randomCount = randomCount;
    }

    @Override
    public String toString() {
        return "GetRandomEntity{" +
                "randomCount=" + randomCount +
                '}';
    }
}
