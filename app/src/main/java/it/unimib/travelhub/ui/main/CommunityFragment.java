package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.database.TravelsDao;
import it.unimib.travelhub.data.database.TravelsRoomDatabase;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.travels.TravelsViewModel;
import it.unimib.travelhub.ui.travels.TravelsViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {
    private TravelsViewModel travelsViewModel;
    Travels travel;

    public CommunityFragment() {
        // Required empty public constructor
    }

    public static CommunityFragment newInstance() {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        // Inflate the layout for this fragment
        travel = new Travels();
        //makeMockTravel(); //TODO: THIS WILL MAKE A TRAVEL FOR TESTING AND SAVE IT IN travel VARIABLE

        Button button = view.findViewById(R.id.button3);
        button.setOnClickListener(v -> {
        //    deleteMockTravel(travel); //TODO: THIS WILL DELETE THE TRAVEL CREATED BY makeMockTravel()
        });

        return view;

    }

    private void makeMockTravel() { //TODO for testing, remove
        Travels travel = new Travels();
        travel.setId((System.currentTimeMillis() + "").hashCode());

        ArrayList<TravelSegment> travelSegments = new ArrayList<>();
        travelSegments.add(new TravelSegment("Milano"));
        travelSegments.add(new TravelSegment("Roma"));
        travel.setDestinations(travelSegments);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        Date parsedDate = null;
        Date endDate = null;
        try {
            parsedDate = sdf.parse("05/06/2024");
            endDate = sdf.parse("09/06/2024");
        } catch (Exception e) {
            e.printStackTrace();
        }
        travel.setStartDate(parsedDate);

        travel.setEndDate(endDate);

        travel.setTitle("PROVAAAA");

        ArrayList<TravelMember> members = new ArrayList<>();
        members.add(new TravelMember("Roberto", "kO2tepwx7Lea92hyRVwITcfL2ak2", TravelMember.Role.CREATOR));
        members.add(new TravelMember("ciao", "oH8EFtZMyhOE7dwmH0XJxzZC1Ar2", TravelMember.Role.MEMBER));

        travel.setMembers(members);

        this.travel = travel;
        travelsViewModel.addTravel(travel);
    }

    private void deleteMockTravel(Travels travel) { //TODO for testing, remove
        Observer<Result> resultObserver = new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result != null && result.isSuccess()) {
                    Result.TravelsResponseSuccess travelResponse = (Result.TravelsResponseSuccess) result;
                    Travels travelDeleted = travelResponse.getData().getTravelsList().get(0);
                    Log.d("CommunityFragment", "Travel deleted: " + travelDeleted);

                } else {
                    Result.Error error = (Result.Error) result;
                    Log.d("CommunityFragment", "Travel not deleted, Error: " + error.getMessage());
                }

                travelsViewModel.deleteTravel(travel).removeObserver(this);
            }
        };
        travelsViewModel.deleteTravel(travel).observe(getViewLifecycleOwner(), resultObserver);
    }
}



