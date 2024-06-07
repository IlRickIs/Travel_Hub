package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String name;
    private String surname;
    private int age;
    private String photoUrl;
    private String email;
    private String idToken;

    public User(String username, String idToken) {
        this.username = username;
        this.idToken = idToken;
    }

    public User() {
        this.username = null;
        this.name = null;
        this.surname = null;
        this.age = 0;
        this.photoUrl = null;
        this.email = null;
        this.idToken=null;
    }

    public User(String username) {
        this.username = username;
    }
    public User(String name, String email, String idToken) {
        this.username = name;
        this.email = email;
        this.idToken = idToken;
    }



    public User(String name, String surname, int age, String photoUrl, String email, String idToken) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.photoUrl = photoUrl;
        this.email = email;
        this.idToken = idToken;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
