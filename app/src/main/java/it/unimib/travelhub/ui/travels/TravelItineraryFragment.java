package it.unimib.travelhub.ui.travels;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TravelSegmentRecyclerAdapter;
import it.unimib.travelhub.adapter.TravelSegmentTimeRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentTravelItineraryBinding;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TravelItineraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelItineraryFragment extends Fragment {


    private FragmentTravelItineraryBinding binding;
    protected RecyclerView.LayoutManager travelLayoutManager;
    private static final String TRAVEL = "travel";
    private Travels travel;

    public TravelItineraryFragment(Travels travel) {
        this.travel = travel;
        // Required empty public constructor
    }

    public static TravelItineraryFragment newInstance(Travels travel) {
        TravelItineraryFragment fragment = new TravelItineraryFragment(travel);
        Bundle args = new Bundle();
        args.putSerializable(TRAVEL, travel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travel = (Travels) getArguments().getSerializable(TRAVEL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTravelItineraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView travelSegmentsRecyclerView = binding.segmentsRecyclerView;
        travelLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        TravelSegmentRecyclerAdapter travelSegmentRecyclerAdapter = new TravelSegmentRecyclerAdapter(travel.getDestinations());
        travelSegmentsRecyclerView.setLayoutManager(travelLayoutManager);
        travelSegmentsRecyclerView.setAdapter(travelSegmentRecyclerAdapter);

    }


    public static void delete_travel_segment(TravelSegment travelSegment) {
        //TODO implement delete_travel_segment
        Log.d("TravelItineraryFragment", "delete_travel_segment: " + travelSegment.toString());
    }
}