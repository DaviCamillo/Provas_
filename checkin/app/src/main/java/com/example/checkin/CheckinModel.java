package com.example.checkin;

import java.io.Serializable;

public class CheckinModel implements Serializable {
    private String name;
    private String cpf;
    private String date;
    private String seat;

    public CheckinModel(String name, String cpf, String date, String seat) {
        this.name = name;
        this.cpf = cpf;
        this.date = date;
        this.seat = seat;
    }

    public String getName() { return name; }
    public String getCpf() { return cpf; }
    public String getDate() { return date; }
    public String getSeat() { return seat; }
}