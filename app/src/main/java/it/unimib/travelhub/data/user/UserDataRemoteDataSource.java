package it.unimib.travelhub.data.user;

import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.unimib.travelhub.model.User;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DatabaseReference databaseReference;

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database" + user);
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                            .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
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
}
