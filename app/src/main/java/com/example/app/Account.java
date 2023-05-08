package com.example.app;

public class Account {
    public String email, pass;
    public int status;

    public Account() {
    }

    public Account(String email, String pass, int status) {
        this.email = email;
        this.pass = pass;
        this.status = status;
    }
}
