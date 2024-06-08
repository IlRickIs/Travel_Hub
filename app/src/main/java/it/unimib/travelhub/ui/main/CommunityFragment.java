package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
        makeMockTravel(); //THIS WILL MAKE A TRAVEL FOR TESTING AND SAVE IT IN travel VARIABLE

        Button button = view.findViewById(R.id.button3);
        button.setOnClickListener(v -> {
            deleteMockTravel(travel); //THIS WILL DELETE THE TRAVEL CREATED BY makeMockTravel()
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        DatabaseReference databaseReference = firebaseDatabase.getReference().getRef();

        databaseReference.child("travels").child(String.valueOf(travel.getId())).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "travel deleted from remote", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
            }
        });

        //delete from users collection
        for (TravelMember member : travel.getMembers()) {
            ArrayList travelsIds = new ArrayList<>();
            databaseReference.child("users").child(member.getIdToken()).child("travels").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    travelsIds.addAll((ArrayList) task.getResult().getValue());
                    Log.d("CommunityFragment", "user: " + member.getUsername() + " travles before remove: " + travelsIds.toString());
                    travelsIds.remove(travel.getId());
                    Log.d("CommunityFragment", "user: " + member.getUsername() + " travles AFTER remove: " + travelsIds.toString());
                    databaseReference.child("users").child(member.getIdToken()).child("travels").setValue(travelsIds);
                }
            });
        }


        //delete from local
        TravelsDao travelsDao = TravelsRoomDatabase.getDatabase(requireContext()).travelsDao();
        TravelsRoomDatabase.databaseWriteExecutor.execute(() -> {
            int c =  travelsDao.delete(travel);
            if (c > 0) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "travel deleted from local", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}