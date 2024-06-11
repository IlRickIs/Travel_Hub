package it.unimib.travelhub.data.user;

import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.travelhub.model.User;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class UserRemoteFirestoreDataSource extends BaseUserDataRemoteDataSource{

    private final SharedPreferencesUtil sharedPreferencesUtil;

    private final FirebaseFirestore firebaseDatabase;

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    public UserRemoteFirestoreDataSource(SharedPreferencesUtil sharedPreferencesUtil, FirebaseFirestore firebaseDatabase) {
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.firebaseDatabase = firebaseDatabase;
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

    @Override
    public void isUsernameAvailable(String username) {

    }

    @Override
    public void isUsernameTaken(String username, String email, String password) {

    }

    @Override
    public void isUserRegistered(String username, UserDataRemoteDataSource.UsernameCheckCallback callback) {

    }
}
