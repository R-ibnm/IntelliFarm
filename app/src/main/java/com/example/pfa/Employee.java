package com.example.pfa;

public class Employee {
    private String name;
    private String prenom;
    private String email;
    private String password;
    private String number;
    private String picture;

    public Employee(){}
    public Employee (String name, String prenom, String email, String password,String number, String picture) {
        this.name = name;
        this.prenom = prenom;
        this.email = email;
        this.password= password;
        this.number = number;
        this.picture = picture;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(String name, String prenom, String email, String password, String number, String picture) {
        return this.name.equals(name)
                && this.prenom.equals(prenom)
                && this.email.equals(email)
                && this.password.equals(password)
                && this.number.equals(number)
                && this.picture.equals(picture);
    }
}