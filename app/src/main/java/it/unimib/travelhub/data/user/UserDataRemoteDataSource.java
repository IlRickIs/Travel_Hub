package it.unimib.travelhub.data.user;

import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.unimib.travelhub.model.User;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DatabaseReference databaseReference;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void saveUserData(User user) {

    }

    @Override
    public void getUserFavorite(String idToken) {

    }

    @Override
    public void getUserPreferences(String idToken) {

    }

    @Override
    public void saveUserPreferences(String preferences) {

    }
}
