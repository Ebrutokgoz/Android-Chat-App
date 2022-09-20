package com.example.chatapplication2.Models;

public class Users {
    String name, photo, about;

    public Users(){
    }

    public Users(String name, String about, String photo){
        this.name = name;
        this.about = about;
        this.photo = photo;
    }

    public String getName(){
        return name;
    }

    public void setName(String ad){
        this.name = name;
    }



    public String getAbout(){
        return about;
    }

    public void setAbout(String about){
        this.about = about;
    }

    public String getPhoto(){
        return photo;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }

}
