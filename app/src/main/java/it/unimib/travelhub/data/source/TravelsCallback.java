package it.unimib.travelhub.data.source;

import it.unimib.travelhub.model.TravelsResponse;

public interface TravelsCallback {
    void onSuccessFromRemote(TravelsResponse travelsResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(TravelsResponse travelsResponse);
    void onFailureFromLocal(Exception exception);
    void onSuccessFromCloudReading(TravelsResponse travelsResponse);
    void onSuccessSynchronization();
    void onSuccessDeletion();
}
