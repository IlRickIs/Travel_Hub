package it.unimib.travelhub.ui.travels;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.travelhub.R;
import it.unimib.travelhub.model.Travels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TravelItineraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelItineraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Travels travel;

    public TravelItineraryFragment(Travels travel) {
        this.travel = travel;
        // Required empty public constructor
    }

    public static TravelItineraryFragment newInstance(Travels travel) {
        TravelItineraryFragment fragment = new TravelItineraryFragment(travel);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel_itinerary, container, false);
    }
}