package com.examplespring.demo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class persons {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstname, lastname, city, datar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstname() {
        return firstname;
    }
    public  void setFirstname (String fn) {
        this.firstname = fn;
    }
    public String getLastname() {
        return lastname;
    }
    public  void setLastname (String ln) {
        this.lastname = ln;
    }
    public String getCity() {
        return city;
    }
    public  void setCity (String ci) {
        this.city = ci;
    }
    public String getDatar () {
        String[] Arr = datar.split("-");
        return Arr[2]+"."+Arr[1]+"."+Arr[0];
    }
    public void setDatar (String dr) {
        this.datar = dr;
    }
}
