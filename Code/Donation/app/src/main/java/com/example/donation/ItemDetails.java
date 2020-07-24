package com.example.donation;

import android.net.Uri;

import java.util.ArrayList;

public class ItemDetails {

    private boolean clothes = false;
    private boolean electronics = false;
    private boolean food = false;
    private boolean toys = false;
    private boolean other = false;
    private String description;
    private String title;
    private Uri photo1;
    private Uri photo2;
    private String bidders;
    private int numberOfBidders;
    private long time;

    public ItemDetails(){

    }

    public boolean getClothes() {
        return clothes;
    }

    public void setClothes(boolean clothes) {
        this.clothes = clothes;
    }

    public boolean getElectronics() {
        return electronics;
    }

    public void setElectronics(boolean electronics) {
        this.electronics = electronics;
    }

    public boolean getFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public boolean getToys() {
        return toys;
    }

    public void setToys(boolean toys) {
        this.toys = toys;
    }

    public boolean getOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getPhoto1() {
        return photo1;
    }

    public void setPhoto1(Uri photo1) {
        this.photo1 = photo1;
    }

    public Uri getPhoto2() {
        return photo2;
    }

    public void setPhoto2(Uri photo2) {
        this.photo2 = photo2;
    }

    public String getBidders(){
        return bidders;
    }

    public void setBidders(String bidders){
         this.bidders = bidders;
    }

    // time remaining
    public long getTime(){
        return time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public int getNumberOfBidders(){
        return numberOfBidders;
    }

    public void setNumberOfBidders(int numberOfBidders){
        this.numberOfBidders = numberOfBidders;
    }
}
