package it.unimib.travelhub.data.repository.travels;

import static it.unimib.travelhub.util.Constants.FRESH_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.travelhub.data.source.BaseTravelsLocalDataSource;
import it.unimib.travelhub.data.source.BaseTravelsRemoteDataSource;
import it.unimib.travelhub.data.source.TravelsCallback;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class TravelsRepository implements ITravelsRepository, TravelsCallback {
    private final BaseTravelsLocalDataSource travelsLocalDataSource;
    private final BaseTravelsRemoteDataSource travelsRemoteDataSource;
    private final MutableLiveData<Result> travelsMutableLiveData;

    public TravelsRepository(BaseTravelsLocalDataSource travelsLocalDataSource, BaseTravelsRemoteDataSource travelsRemoteDataSource) {
        this.travelsLocalDataSource = travelsLocalDataSource;
        this.travelsRemoteDataSource = travelsRemoteDataSource;
        travelsMutableLiveData = new MutableLiveData<>();
        this.travelsLocalDataSource.setTravelsCallback(this);
        this.travelsRemoteDataSource.setTravelsCallback(this);
    }
    @Override
    public MutableLiveData<Result> fetchTravels(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            travelsRemoteDataSource.getAllUserTravel();
        } else {
            travelsLocalDataSource.getTravels();
        }
        travelsRemoteDataSource.getAllUserTravel();
        return travelsMutableLiveData;
    }

    @Override
    public void updateTravel(Travels travel) {
        travelsLocalDataSource.updateTravel(travel);
    }

    @Override
    public void addTravels(List<Travels> travelsList) {
        travelsLocalDataSource.insertTravels(travelsList);
    }

    public void addTravel(Travels travel) {
        travelsRemoteDataSource.addTravel(travel);
    }

    @Override
    public void onSuccessFromRemote(TravelsResponse travelsResponse, long lastUpdate) {
        travelsLocalDataSource.insertTravels(travelsResponse.getTravelsList());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        travelsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromLocal(TravelsResponse travelsResponse) {
        if (travelsMutableLiveData.getValue() != null && travelsMutableLiveData.getValue().isSuccess()) {
            List<Travels> travelsList = ((Result.TravelsResponseSuccess)travelsMutableLiveData.getValue()).getData().getTravelsList();
            travelsList.addAll(travelsResponse.getTravelsList());
            travelsResponse.setTravelsList(travelsList);
            Result.TravelsResponseSuccess result = new Result.TravelsResponseSuccess(travelsResponse);
            travelsMutableLiveData.postValue(result);
        } else {
            Result.TravelsResponseSuccess result = new Result.TravelsResponseSuccess(travelsResponse);
            travelsMutableLiveData.postValue(result);
        }
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
    public void onSuccessSynchronization() {

    }

    @Override
    public void onSuccessDeletion() {

    }
}
