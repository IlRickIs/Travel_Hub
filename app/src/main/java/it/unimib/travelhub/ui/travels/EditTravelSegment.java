package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.DESTINATIONS_HINTS;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_TEXTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_HINTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_TEXTS;
import static it.unimib.travelhub.util.Constants.TRAVEL_DESCRIPTION;
import static it.unimib.travelhub.util.Constants.TRAVEL_TITLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.ActivityAddTravelBinding;
import it.unimib.travelhub.databinding.FragmentEditTravelBinding;
import it.unimib.travelhub.databinding.FragmentEditTravelSegmentBinding;

public class EditTravelSegment extends Fragment {

    private FragmentEditTravelSegmentBinding binding;

    private String destinationName;
    private String dateFrom;
    private String dateTo;

    private String description;

    public EditTravelSegment() {
        // Required empty public constructor
    }

    public static EditTravelSegment newInstance() {
        EditTravelSegment fragment = new EditTravelSegment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
           // binding.destinationFormEditText.setText(savedInstanceState.getString(DESTINATIONS_TEXTS));
            binding.descriptionFormEditText.setText(savedInstanceState.getString(TRAVEL_DESCRIPTION));
        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditTravelSegmentBinding.inflate(inflater, container, false);

        binding.addDestinationButtonSegment.setOnClickListener(v -> {
            // change fragment and pass the data
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activityAddFragmentContainerView, new EditTravelSegment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}