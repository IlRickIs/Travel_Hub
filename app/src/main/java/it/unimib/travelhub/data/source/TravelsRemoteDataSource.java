package it.unimib.travelhub.data.source;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class TravelsRemoteDataSource extends BaseTravelsRemoteDataSource {

    private static final String TAG = TravelsRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    private final DataEncryptionUtil dataEncryptionUtil;

    public TravelsRemoteDataSource(DataEncryptionUtil dataEncryptionUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getAllUserTravel() {
        try {
            String idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).child("travels").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error getting data", task.getException());
                } else {
                    Log.d(TAG, "Successful read: " + task.getResult().getValue());

                    List<Integer> travelsIdList = new ArrayList<>();
                    for(DataSnapshot ds : task.getResult().getChildren()) {
                        Integer id = ds.getValue(Integer.class);
                        travelsIdList.add(id);
                    }

                    Log.d(TAG, "Travels id list: " + travelsIdList);

                    TravelsResponse travelsResponse = new TravelsResponse(travelsIdList.size(), travelsCallback);

                    for (Integer id : travelsIdList) {
                        databaseReference.child("travels").child(id.toString()).get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.d(TAG, "Error getting data", task1.getException());
                            } else {
                                Log.d(TAG, "Successful read: " + task1.getResult().getValue());
                                Travels travels = task1.getResult().getValue(Travels.class);
                                travelsResponse.addTravel(travels);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTravel(Travels travel) {
        try {
            String idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            databaseReference.child("travels").child(Long.toString(travel.getId())).setValue(travel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //travelsCallback.onSuccessFromCloudWriting();
                            Log.d(TAG, "Travel added successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //travelsCallback.onFailureFromCloud(e);
                            Log.d(TAG, "Error adding travel", e);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTravel(Travels travels) {

    }

    @Override
    public void deleteTravel(Travels travels) {

    }

    @Override
    public void deleteAllTravels() {

    }
}
