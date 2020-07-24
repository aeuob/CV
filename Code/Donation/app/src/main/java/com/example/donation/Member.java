package com.example.donation;

public class Member {

    private static String email;
    private String firstName;
    private String secondName;
    private String password;
    private String retypedPassword;
    private String address;
    private boolean anonymous;
    private boolean canReceive;
    private float positive;
    private float negative;

    public Member(){
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isVerified() {
        return canReceive;
    }

    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

    public float  getPositiveFeedback(){
        return positive;
    }

    public void setPositiveFeedback(float positive){
        this.positive = positive;
    }

    public float  getNegativeFeedback(){
        return positive;
    }

    public void setNegativeFeedback(float negative){
        this.negative = negative;
    }

}
