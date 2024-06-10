package it.unimib.travelhub.data.repository.travels;

import static it.unimib.travelhub.util.Constants.FRESH_TIMEOUT;
import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.travelhub.data.source.BaseTravelsLocalDataSource;
import it.unimib.travelhub.data.source.BaseTravelsRemoteDataSource;
import it.unimib.travelhub.data.source.TravelsCallback;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class TravelsRepository implements ITravelsRepository, TravelsCallback {
    private final BaseTravelsLocalDataSource travelsLocalDataSource;
    private final BaseTravelsRemoteDataSource travelsRemoteDataSource;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final MutableLiveData<Result> travelsMutableLiveData;

    public TravelsRepository(BaseTravelsLocalDataSource travelsLocalDataSource,
                             BaseTravelsRemoteDataSource travelsRemoteDataSource,
                             SharedPreferencesUtil sharedPreferencesUtil) {
        this.travelsLocalDataSource = travelsLocalDataSource;
        this.travelsRemoteDataSource = travelsRemoteDataSource;
        travelsMutableLiveData = new MutableLiveData<>();
        this.travelsLocalDataSource.setTravelsCallback(this);
        this.travelsRemoteDataSource.setTravelsCallback(this);
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }
    @Override
    public MutableLiveData<Result> fetchTravels(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        Log.d("TravelsRepository", "Current time: " + currentTime);
        Log.d("TravelsRepository", "Last update: " + lastUpdate);

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            travelsRemoteDataSource.getAllUserTravel();
            Log.d("TravelsRepository", "Remote data source");
        } else {
            travelsLocalDataSource.getTravels();
            Log.d("TravelsRepository", "Local data source");
        }
        //travelsRemoteDataSource.getAllUserTravel();
        return travelsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> deleteTravel(Travels travel) {
        travelsRemoteDataSource.deleteTravel(travel);
        return travelsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> updateTravel(Travels travel) {
        travelsRemoteDataSource.updateTravel(travel);
        return travelsMutableLiveData;
    }

    @Override
    public void addTravels(List<Travels> travelsList) {
        travelsLocalDataSource.insertTravels(travelsList);
    }

    public MutableLiveData<Result> addTravel(Travels travel) {
        travelsRemoteDataSource.addTravel(travel);
        return travelsMutableLiveData;
    }

    public void deleteAll() {
        travelsLocalDataSource.deleteAll();
    }

    @Override
    public void
    onSuccessFromRemote(TravelsResponse travelsResponse, long lastUpdate) {
        sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE,
                String.valueOf(System.currentTimeMillis()));
        travelsLocalDataSource.deleteAllAfterSync(travelsResponse.getTravelsList());
        //travelsLocalDataSource.insertTravels(travelsResponse.getTravelsList());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        travelsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromLocal(TravelsResponse travelsResponse) {
        if (travelsMutableLiveData.getValue() != null && travelsMutableLiveData.getValue().isSuccess()) {
            ArrayList<Travels> travelsList = addDifferentTravels( //TODO: Vedere se serve
                    ((Result.TravelsResponseSuccess)travelsMutableLiveData.getValue()).getData().getTravelsList(),
                    travelsResponse.getTravelsList());
            travelsResponse.setTravelsList(travelsList);
            Result.TravelsResponseSuccess result = new Result.TravelsResponseSuccess(travelsResponse);
            travelsMutableLiveData.postValue(result);
        } else {
            Result.TravelsResponseSuccess result = new Result.TravelsResponseSuccess(travelsResponse);
            travelsMutableLiveData.postValue(result);
        }
    }

    private ArrayList<Travels> addDifferentTravels(List<Travels> travelsListMLD, List<Travels> travelsList) {
        ArrayList<Travels> newTravelsList = new ArrayList<>(travelsListMLD);
        for (Travels travel : travelsList) {
            if (!travelsListMLD.contains(travel)) {
                newTravelsList.add(travel);
            }
        }
        return newTravelsList;
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        travelsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromCloudReading(TravelsResponse travelsResponse) {
        if (travelsResponse != null) {
            travelsLocalDataSource.insertTravels(travelsResponse.getTravelsList());
            travelsMutableLiveData.postValue(new Result.TravelsResponseSuccess(travelsResponse));
        }
    }

    @Override
    public void onSuccessSynchronization(Travels travel) {
        List<Travels> travelList = new ArrayList<>();
        travelList.add(travel);
        TravelsResponse travelsResponse = new TravelsResponse(travelList);
        travelsMutableLiveData.postValue(new Result.TravelsResponseSuccess(travelsResponse));
    }

    @Override
    public void onSuccessDeletion() {
        Log.d("TravelsRepository", "Travels deleted");
    }

    public void onSuccessDeletionAfterSync(List<Travels> travelsList) {
        travelsLocalDataSource.insertTravels(travelsList);
    }

    @Override
    public void onSuccessDeletionFromLocal(Travels travel) {
        List<Travels> travelList = new ArrayList<>();
        travelList.add(travel);
        travelsMutableLiveData.postValue(new Result.TravelsResponseSuccess(new TravelsResponse(travelList)));
    }

    @Override
    public void onUpdateSuccess(Travels travel) {
        travelsLocalDataSource.updateTravel(travel);
    }

    @Override
    public void onSuccessDeletionFromRemote(Travels travel) {
        travelsLocalDataSource.deleteTravel(travel);
    }

    @Override
    public void onSuccessFromCloudWriting(Travels travel) {
        List<Travels> travelList= new ArrayList<>();
        travelList.add(travel);
        travelsLocalDataSource.insertTravels(travelList);
    }
}
