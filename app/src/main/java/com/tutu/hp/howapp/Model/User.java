package com.tutu.hp.howapp.Model;
//User class
public class User {
    private String ID;
    private String UserName;
    private String PhoneNumber;

    private String status;


    public User(String ID, String UserName, String PhoneNumber, String status) {
        this.ID = ID;
        this.UserName = UserName;
        this.PhoneNumber = PhoneNumber;
        this.status=status;
    }

    public User(){

    }

    public String getId() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return UserName;
    }

    public void setUsername(String UserName) {
        this.UserName = UserName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }

}
