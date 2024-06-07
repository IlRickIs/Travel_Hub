package it.unimib.travelhub.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.travels.TravelsViewModel;
import it.unimib.travelhub.ui.travels.TravelsViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;

public class MapFragment extends Fragment {

    private TravelsViewModel travelsViewModel;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ITravelsRepository travelsRepository =
                ServiceLocator.getInstance().getTravelsRepository(
                        requireActivity().getApplication()
                );
        if (travelsRepository != null) {
            // This is the way to create a ViewModel with custom parameters
            // (see NewsViewModelFactory class for the implementation details)
            travelsViewModel = new ViewModelProvider(
                    requireActivity(),
                    new TravelsViewModelFactory(travelsRepository)).get(TravelsViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //makeMockTravel();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    private void makeMockTravel() { //TODO for testing, remove
        Travels travel = new Travels();
        travel.setId((System.currentTimeMillis() + "").hashCode());

        ArrayList<TravelSegment> travelSegments = new ArrayList<>();
        travelSegments.add(new TravelSegment("Milano"));
        travelSegments.add(new TravelSegment("Roma"));
        travel.setDestinations(travelSegments);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        Date parsedDate = null;
        Date endDate = null;
        try {
            parsedDate = sdf.parse("09/06/2024");
            endDate = sdf.parse("11/06/2024");
        } catch (Exception e) {
            e.printStackTrace();
        }
        travel.setStartDate(parsedDate);

        travel.setEndDate(endDate);

        travel.setTitle("FUTURO");

        ArrayList<TravelMember> members = new ArrayList<>();
        members.add(new TravelMember("Roberto", "kO2tepwx7Lea92hyRVwITcfL2ak2" ,TravelMember.Role.CREATOR));

        travel.setMembers(members);

        travelsViewModel.addTravel(travel);
    }
}