package it.unimib.travelhub.ui.travels;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;

public class TravelsViewModel extends ViewModel {

        private static final String TAG = TravelsViewModel.class.getSimpleName();

        private final ITravelsRepository travelsRepository;
        private MutableLiveData<Result> travelsListLiveData;

        public TravelsViewModel(ITravelsRepository iTravelsRepository) {
            this.travelsRepository = iTravelsRepository;
        }

        /**
        * Returns the LiveData object associated with the
        * travels list to the Fragment/Activity.
        * @return The LiveData object associated with the travels list.
        */
        public MutableLiveData<Result> getTravels(long lastUpdate) {
            if (travelsListLiveData == null) { //TODO FIX THIS METHOD
                fetchTravels(lastUpdate);
                Log.d(TAG, "Travels list fetched");
            }else{
                updateLiveData(lastUpdate);
                Log.d(TAG, "Travels list updated");
            }
            Log.d(TAG, "Travels list: " + travelsListLiveData.getValue());
            return travelsListLiveData;
        }

        private void updateLiveData(long lastUpdate) {
            travelsRepository.updateExistingLiveData(lastUpdate);
        }
        /**
        * Fetches the travels list from the repository.
        */
        private void fetchTravels(long lastUpdate) {
            travelsListLiveData = travelsRepository.fetchTravels(lastUpdate);
        }

        /**
        * Updates the travel status.
        * @param travel The travel to be updated.
        */
        public void updateTravel(Travels travel) {
            travelsRepository.updateTravel(travel);
        }

//        public void addTravel(Travels travel) {
//            travelsListLiveData = travelsRepository.addTravel(travel);
//            Observer<Result> observer = new Observer<Result>() {
//                @Override
//                public void onChanged(Result result) {
//                    if (result.isSuccess()) {
//                        fetchTravels(System.currentTimeMillis());
//                    }
//                    travelsListLiveData.setValue(result);
//                    Log.d(TAG, "Travel ADDED: " + travelsListLiveData.getValue());
//                    travelsListLiveData.removeObserver(this);
//                }
//            };
//            travelsListLiveData.observeForever(observer);
//        }

        public void addTravel(Travels travel) {
            travelsListLiveData = travelsRepository.addTravel(travel);
        }

        public MutableLiveData<Result> getTravelAddLiveData() {
            if (travelsListLiveData == null) {
                travelsListLiveData = new MutableLiveData<>();
            }
            return travelsListLiveData;
        }
}
