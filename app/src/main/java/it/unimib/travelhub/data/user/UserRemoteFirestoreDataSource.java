package it.unimib.travelhub.data.user;

import static it.unimib.travelhub.util.Constants.FIREBASE_USERNAMES_COLLECTION;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.travelhub.util.Constants.USERNAME;
import static it.unimib.travelhub.util.Constants.USERNAME_NOT_AVAILABLE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class UserRemoteFirestoreDataSource extends BaseUserDataRemoteDataSource{

    private final SharedPreferencesUtil sharedPreferencesUtil;

    private final FirebaseFirestore firebaseDatabase;

    private static final String TAG = UserRemoteFirestoreDataSource.class.getSimpleName();

    public UserRemoteFirestoreDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.firebaseDatabase = FirebaseFirestore.getInstance();
    }
    @Override
    public void saveUserData(User user) {
        firebaseDatabase.collection(FIREBASE_USERS_COLLECTION).document(user.getIdToken()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User newUser = document.toObject(User.class);
                            Log.d(TAG, "User already present in Firebase Realtime Database");
                            userResponseCallback.onSuccessFromRemoteDatabase(newUser);
                        } else {
                            Log.d(TAG, "User not present in Firebase Realtime Database" + user);
                            firebaseDatabase.collection(FIREBASE_USERS_COLLECTION).document(user.getIdToken()).set(user, SetOptions.merge())
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + task1.getResult());
                                            mapUsernameToId(user);
                                        } else {
                                            Log.d(TAG, "Error adding document", task1.getException());
                                            userResponseCallback.onFailureFromRemoteDatabase(task1.getException().toString());
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Error getting document", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().toString());
                    }
                });

    }

    private void mapUsernameToId(User user) {
        HashMap<String, Object> username = new HashMap<>();
        username.put("id", user.getIdToken());
        firebaseDatabase.collection(FIREBASE_USERNAMES_COLLECTION).document(user.getUsername()).set(username)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + task.getResult());
                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                    } else {
                        Log.d(TAG, "Error adding document", task.getException());
                    }
                });
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
        firebaseDatabase.collection(FIREBASE_USERS_COLLECTION).document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "User already present in Firebase Realtime Database");
                            userResponseCallback.onFailureFromRemoteDatabase(USERNAME_NOT_AVAILABLE);
                        } else {
                            Log.d(TAG, "User not present in Firebase Realtime Database");
                            userResponseCallback.signUp(username, email, password);
                        }
                    } else {
                        Log.d(TAG, "Error getting document", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().toString());
                    }
                });
    }


    public interface UsernameCheckCallback {
        void onUsernameResponse(Result result);
    }
    @Override
    public void isUserRegistered(String username, UserDataRemoteDataSource.UsernameCheckCallback callback) {
        firebaseDatabase.collection(FIREBASE_USERNAMES_COLLECTION).document(username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().getData() != null) {
                            Log.d(TAG, "checkIfUserExists: " + task.getResult().getData());
                            User u = new User();
                            u.setUsername(username);
                            u.setIdToken((String) task.getResult().getData().get("id"));
                            callback.onUsernameResponse(new Result.UserResponseSuccess(u));
                        } else {
                            callback.onUsernameResponse(new Result.Error("User not found"));
                        }
                    } else {
                        Log.d(TAG, "Error getting document", task.getException());
                        callback.onUsernameResponse(new Result.Error(task.getException().toString()));
                    }
                });
    }
}
