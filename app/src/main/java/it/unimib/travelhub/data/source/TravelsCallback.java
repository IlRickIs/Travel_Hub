package it.unimib.travelhub.data.source;

import java.util.List;

import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public interface TravelsCallback {
    void onSuccessFromRemote(TravelsResponse travelsResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(TravelsResponse travelsResponse);
    void onFailureFromLocal(Exception exception);
    void onSuccessFromCloudReading(TravelsResponse travelsResponse);
    void onSuccessSynchronization();
    void onSuccessDeletion();
    void onSuccessDeletion(Exception exception);
    void onSuccessFromCloudWriting(Travels travel);
    void onSuccessDeletionAfterSync(List<Travels> travelsList);
}
