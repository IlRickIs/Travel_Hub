package it.unimib.travelhub.data.repository.travels;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;

public interface ITravelsRepository {
    MutableLiveData<Result> fetchTravels(long lastUpdate);

    MutableLiveData<Result> deleteTravel(Travels travel);
    void updateTravel(Travels travel);
    void addTravels(List<Travels> travelsList);

    MutableLiveData<Result> addTravel(Travels travel);

    void deleteAll();


}
