package it.unimib.travelhub.ui.travels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import it.unimib.travelhub.databinding.ActivityTravelBinding;
import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.UsersRecyclerAdapter;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.User;

public class TravelActivity extends AppCompatActivity {

    protected RecyclerView.LayoutManager mLayoutManager;

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
        binding.travelDescription.setText(travel.getDescription());

        String startDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getStartDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getEndDate());
        binding.travelData.setText(startDate + " - " + endDate);


        RecyclerView recyclerView = binding.friendsRecyclerView;
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        ArrayList<TravelMember> dataSource = new ArrayList<>(travel.getMembers());

        UsersRecyclerAdapter usersRecyclerAdapter = new UsersRecyclerAdapter(dataSource);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(usersRecyclerAdapter);
        findViewById(R.id.buttonBack).setOnClickListener(v -> this.getOnBackPressedDispatcher().onBackPressed());
    }

}