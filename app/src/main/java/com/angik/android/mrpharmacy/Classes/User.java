package com.angik.android.mrpharmacy.Classes;

public class User {
    private String name;
    private String phone;
    private String pass;
    private String add;

    public User() {
    }

    public User(String name, String phone, String pass, String add) {
        this.name = name;
        this.phone = phone;
        this.pass = pass;
        this.add = add;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }
}
