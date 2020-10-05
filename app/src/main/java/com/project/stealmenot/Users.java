package com.project.stealmenot;

public class Users {

    public String name,email,city,password,mobile,imeiNumber;

    public Users()
    {

    }

    public Users(String name, String email,String password, String city, String mobile,String imeiNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.city = city;
        this.mobile = mobile;
        this.imeiNumber=imeiNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }
}
