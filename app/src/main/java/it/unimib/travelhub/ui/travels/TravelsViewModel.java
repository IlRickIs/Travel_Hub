package it.unimib.travelhub.ui.travels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;

public class TravelsViewModel extends ViewModel {

        private static final String TAG = TravelsViewModel.class.getSimpleName();

        private final ITravelsRepository travelsRepository;
        private MutableLiveData<Result> travelsListLiveData;

        private MutableLiveData<Result> travelAddLiveData;

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
            }
            return travelsListLiveData;
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

        public void addTravel(Travels travel) {
            travelAddLiveData = travelsRepository.addTravel(travel);
        }

        public MutableLiveData<Result> getTravelAddLiveData() {
            if (travelAddLiveData == null) {
                travelAddLiveData = new MutableLiveData<>();
            }
            return travelAddLiveData;
        }
}
