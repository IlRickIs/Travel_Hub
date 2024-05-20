package it.unimib.travelhub.ui.travels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
        public MutableLiveData<Result> getTravels() {
            if (travelsListLiveData == null) {
                fetchTravels();
            }
            return travelsListLiveData;
        }

        /**
        * Fetches the travels list from the repository.
        */
        private void fetchTravels() {
            travelsListLiveData = travelsRepository.fetchTravels();
        }

        /**
        * Updates the travel status.
        * @param travel The travel to be updated.
        */
        public void updateTravel(Travels travel) {
            travelsRepository.updateTravel(travel);
        }
}
