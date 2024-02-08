package it.unimib.travelhub.ui.travels;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.travelhub.R;

public class EditTravelImagesFragment extends Fragment {


    public EditTravelImagesFragment() {
        // Required empty public constructor
    }

    public static EditTravelImagesFragment newInstance() {
        EditTravelImagesFragment fragment = new EditTravelImagesFragment();
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
        return inflater.inflate(R.layout.fragment_edit_travel_images, container, false);
    }
}