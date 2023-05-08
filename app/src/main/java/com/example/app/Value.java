package com.example.app;

public class Value {
    public float speed, ph, tds, total;
    public String time;

    public Value() {
    }

    public Value(int speed, int ph, int tds, int total, String time) {
        this.speed = speed;
        this.ph = ph;
        this.tds = tds;
        this.total = total;
        this.time = time;
    }
}
