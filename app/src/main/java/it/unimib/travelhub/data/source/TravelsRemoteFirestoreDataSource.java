package it.unimib.travelhub.data.source;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.FIREBASE_TRAVELS_COLLECTION;
import static it.unimib.travelhub.util.Constants.FIREBASE_TRAVELS_MEMBERS_COLLECTION;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class TravelsRemoteFirestoreDataSource extends BaseTravelsRemoteDataSource{

    private final DataEncryptionUtil dataEncryptionUtil;
    private final FirebaseFirestore db;

    private static final String TAG = TravelsRemoteFirestoreDataSource.class.getSimpleName();

    public TravelsRemoteFirestoreDataSource(DataEncryptionUtil dataEncryptionUtil) {
        this.dataEncryptionUtil = dataEncryptionUtil;
        this.db = FirebaseFirestore.getInstance();

    }

    @Override
    public void getAllUserTravel() {
        String idToken = null;
        try {
            idToken = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
        }
        catch(Exception e){
            Log.e(TAG, "Error reading idToken from SharedPreferences", e);
            travelsCallback.onFailureFromRemote(e);
        }

        db.collection(FIREBASE_USERS_COLLECTION).document(idToken)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "get: " + task.getResult().getData().keySet());
                        List<Long> travelIds = (List<Long>) task.getResult().getData().get("travels");

                        if(travelIds == null) {
                            Log.d(TAG, "No travels found");
                            travelsCallback.onFailureFromRemote(new Exception("No travels found"));
                            return;
                        }
                        TravelsResponse travelsResponse = new TravelsResponse(travelIds.size(), travelsCallback);

                        for(long travelId : travelIds) {
                            db.collection(FIREBASE_TRAVELS_COLLECTION).document(String.valueOf(travelId))
                                    .get().addOnCompleteListener(travelTask -> {
                                        if (travelTask.isSuccessful()) {
                                            DocumentSnapshot document = travelTask.getResult();
                                            if (document.exists()) {
                                                Travels travel = document.toObject(Travels.class);
                                                travelsResponse.addTravel(travel);
                                            } else {
                                                Log.d(TAG, "No such document");
                                                travelsCallback.onFailureFromRemote(new Exception("No such document"));
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", travelTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Error adding document", task.getException());
                    }
                });

    }

    @Override
    public void addTravel(Travels travel) {
        Log.d(TAG, "Adding travel: " + travel);
        Map<String, Object> travelMap = travel.toMap();
        db.collection(FIREBASE_TRAVELS_COLLECTION).document(String.valueOf(travel.getId()))
                .set(travelMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Travel added successfully");
                    addToUsers(travel);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding travel", e);
                    travelsCallback.onFailureFromRemote(e);
                });
    }

    private void addToUsers(Travels travel) {
        Log.d(TAG, "Adding to users collection: " + travel);
        List<TravelMember> members = travel.getMembers();
        WriteBatch batch = db.batch();

        for(TravelMember member : members) {
            String userId = member.getIdToken();
            DocumentReference userTravelRef = db.collection(FIREBASE_USERS_COLLECTION)
                    .document(userId);

            Map<String, Object> userTravelMap = new HashMap<>();
            userTravelMap.put("travels", FieldValue.arrayUnion(travel.getId()));
            batch.update(userTravelRef, userTravelMap);
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Travel added to users successfully");
                    travelsCallback.onSuccessFromCloudWriting(travel);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding travel to users", e);
                    travelsCallback.onFailureFromRemote(e);
                });
    }

    @Override
    public void updateTravel(Travels newTravel, Travels oldTravel) {

    }

    @Override
    public void deleteTravel(Travels travels) {

    }

    @Override
    public void deleteAllTravels() {

    }
}
