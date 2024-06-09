package it.unimib.travelhub.ui.travels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import it.unimib.travelhub.adapter.TravelSegmentRecyclerAdapter;
import it.unimib.travelhub.databinding.ActivityTravelBinding;
import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.UsersRecyclerAdapter;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.User;

public class TravelActivity extends AppCompatActivity {

    protected RecyclerView.LayoutManager mLayoutManager;

    protected RecyclerView.LayoutManager travelLayoutManager;
    protected RecyclerView travelSegmentsRecyclerView;

    private ActivityTravelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTravelBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        TravelActivityArgs args = TravelActivityArgs.fromBundle(getIntent().getExtras());
        Travels travel = args.getTravel();

        binding.travelTitle.setText(travel.getTitle());
        binding.buttonBack.setOnClickListener(v -> finish());


        FrameLayout standardBottomSheet = findViewById(R.id.standard_bottom_sheet);
        BottomSheetBehavior<View> standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);


        binding.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TravelActivity.this);
                View view1 = LayoutInflater.from(TravelActivity.this).inflate(R.layout.bottom_sheet_layout_travel, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton buttonDelete = view1.findViewById(R.id.button_Delete);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTravel(travel); // TODO implement deleteTravel
                    }
                });

            }
        });

        if (travel.getDescription() == null || travel.getDescription().isEmpty()) {
            binding.menuDescription.setText(R.string.travel_no_description);
        } else {
            binding.travelDescription.setText(travel.getDescription());
        }

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



        String startDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getStartDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getEndDate());
        binding.travelData.setText(startDate + " - " + endDate);



        long diff = travel.getEndDate().getTime() - travel.getStartDate().getTime();
        long diffToday = System.currentTimeMillis() - travel.getStartDate().getTime();

        if (diffToday < 0) {
            diffToday = 0;
        }
        int progress = (int) (diffToday * 100 / diff);

        binding.travelDuration.setText(String.valueOf(diff / (1000 * 60 * 60 * 24)));
        binding.travelStart.setText(travel.getDestinations().get(0).getLocation());
        binding.progressBar.setProgress(progress);

        RecyclerView travelSegmentsRecyclerView = binding.segmentsRecyclerView;
        travelLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        TravelSegmentRecyclerAdapter travelSegmentRecyclerAdapter = new TravelSegmentRecyclerAdapter(travel.getDestinations());
        travelSegmentsRecyclerView.setLayoutManager(travelLayoutManager);
        travelSegmentsRecyclerView.setAdapter(travelSegmentRecyclerAdapter);


        RecyclerView recyclerView = binding.friendsRecyclerView;
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ArrayList<TravelMember> dataSource = new ArrayList<>(travel.getMembers());
        UsersRecyclerAdapter usersRecyclerAdapter = new UsersRecyclerAdapter(dataSource, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(usersRecyclerAdapter);
    }

    private void deleteTravel(Travels travel) {
        Log.d("TravelActivity", "deleteTravel: " + travel);
    }

}