package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {
    private String username;

    private String name;
    private String surname;
    private int age;
    private String photoUrl;
    private String email;
    private String idToken;

    public User() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.idToken);
    }

    public void readFromParcel(Parcel source) {
        this.username = source.readString();
        this.email = source.readString();
        this.idToken = source.readString();
    }

    protected User(Parcel in) {
        this.username = in.readString();
        this.email = in.readString();
        this.idToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
