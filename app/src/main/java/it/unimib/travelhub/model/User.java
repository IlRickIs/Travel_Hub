package it.unimib.travelhub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {
    private String userName;
    private String email;
    private String idToken;

    public User(String name, String email, String idToken) {
        this.userName = name;
        this.email = email;
        this.idToken = idToken;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
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
                "name='" + userName + '\'' +
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
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeString(this.idToken);
    }

    public void readFromParcel(Parcel source) {
        this.userName = source.readString();
        this.email = source.readString();
        this.idToken = source.readString();
    }

    protected User(Parcel in) {
        this.userName = in.readString();
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
}
