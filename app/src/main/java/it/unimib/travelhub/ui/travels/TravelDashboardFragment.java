package it.unimib.travelhub.ui.travels;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TravelSegmentRecyclerAdapter;
import it.unimib.travelhub.adapter.UsersRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentTravelDashboardBinding;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.Travels;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TravelDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelDashboardFragment extends Fragment {

    private static final String TRAVEL = "travel";
    protected RecyclerView.LayoutManager mLayoutManager;
    private FragmentTravelDashboardBinding binding;
    private Travels travel;

    private boolean isEditing = false;

    public TravelDashboardFragment(Travels travel) {
        // Required empty public constructor
        this.travel = travel;
    }


    public static TravelDashboardFragment newInstance(Travels travel) {
        TravelDashboardFragment fragment = new TravelDashboardFragment(travel);
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

        binding = FragmentTravelDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (travel.getDescription() == null || travel.getDescription().isEmpty()) {
            binding.menuDescription.setText(R.string.travel_no_description);
        } else {
            binding.travelDescription.setText(travel.getDescription());
        }

        editTravel();

        binding.travelDescription.setVisibility(View.GONE);
        binding.segmentsRecyclerView.setVisibility(View.GONE);
        binding.menuDescription.setOnClickListener(v -> {
            if (binding.travelDescription.getVisibility() == View.GONE) {
                binding.travelDescription.setVisibility(View.VISIBLE);
                binding.menuDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_up_24, 0);
            } else {
                binding.travelDescription.setVisibility(View.GONE);
                binding.menuDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
            }
        });
        binding.menuItinerary.setOnClickListener(v -> {
            if (binding.segmentsRecyclerView.getVisibility() == View.GONE) {
                binding.segmentsRecyclerView.setVisibility(View.VISIBLE);
                binding.menuItinerary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_up_24, 0);
            } else {
                binding.segmentsRecyclerView.setVisibility(View.GONE);
                binding.menuItinerary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
            }
        });

        long diff = travel.getEndDate().getTime() - travel.getStartDate().getTime();
        long diffToday = System.currentTimeMillis() - travel.getStartDate().getTime();
        diffToday = diffToday < 0 ? 0 : diffToday;

        int progress = (int) (diffToday * 100 / diff);

        binding.travelDuration.setText(String.valueOf(diff / (1000 * 60 * 60 * 24)));
        binding.travelStart.setText(travel.getDestinations().get(0).getLocation());
        binding.progressBar.setProgress(progress);

        RecyclerView recyclerView = binding.friendsRecyclerView;
        mLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        ArrayList<TravelMember> dataSource = new ArrayList<>(travel.getMembers());
        UsersRecyclerAdapter usersRecyclerAdapter = new UsersRecyclerAdapter(dataSource, 2, "#000000");
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(usersRecyclerAdapter);
    }

    private void editTravel() {
        TravelActivity travelActivity = (TravelActivity) requireActivity();
        TextInputEditText travelDescription = binding.travelDescription;

        travelDescription.setOnClickListener(v -> {
            travelDescription.setFocusableInTouchMode(true);
            travelDescription.setFocusable(true);
            travelDescription.requestFocus();
            travelDescription.setOnFocusChangeListener((v1, hasFocus) -> {
                if (!hasFocus) {
                    travelDescription.setFocusable(false);
                }
            });
        });
        travelDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travel.setDescription(s.toString());
                travelActivity.showEditButton();
            }
            @Override
            public void afterTextChanged(Editable s) {
                travelDescription.setFocusable(false);
            }
        });

    }
}