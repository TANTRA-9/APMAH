package com.example.apmah.Data;

public class UserData {

    String Name;
    String Gmail;
    String Pass;

    public UserData() {
    }

    public UserData(String name, String gmail, String pass) {
        Name = name;
        Gmail = gmail;
        Pass = pass;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGmail() {
        return Gmail;
    }

    public void setGmail(String gmail) {
        Gmail = gmail;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        Pass = pass;
    }
}
