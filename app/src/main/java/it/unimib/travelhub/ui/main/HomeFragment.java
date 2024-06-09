package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;
import static it.unimib.travelhub.util.Constants.TRAVEL_ADDED;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.adapter.TravelSegmentRecyclerAdapter;
import it.unimib.travelhub.adapter.UsersRecyclerAdapter;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentHomeBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;
import it.unimib.travelhub.ui.main.profile.ProfileFragmentDirections;
import it.unimib.travelhub.ui.travels.TravelsViewModel;
import it.unimib.travelhub.ui.travels.TravelsViewModelFactory;
import it.unimib.travelhub.util.JSONParserUtil;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;
    private TravelsViewModel travelsViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TravelsResponse travelsResponse;

    private Travels onGoingTravel;

    private Travels futureTravel;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView friendsRecyclerView;
    protected RecyclerView travelSegmentsRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
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

        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        Intent intent = requireActivity().getIntent();
        if (intent.getBooleanExtra(TRAVEL_ADDED, false)) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    "TRAVEL ADDED SUCCESSFULLY",
                    Snackbar.LENGTH_SHORT).show();
            intent.removeExtra(TRAVEL_ADDED);
        }

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        friendsRecyclerView = binding.friendsRecyclerView;
//        travelSegmentsRecyclerView = binding.segmentsRecyclerView;



        travelsViewModel.getTravels(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "TravelsResponse: " + ((Result.TravelsResponseSuccess) result).getData());
                        travelsResponse = ((Result.TravelsResponseSuccess) result).getData();
                        onGoingTravel = travelsResponse.getOnGoingTravel();
                        futureTravel = travelsResponse.getFutureTravel();
                        Travels doneTravel = travelsResponse.getDoneTravel();

                        if (onGoingTravel == null) {

                            binding.homeCardNoTravel.setVisibility(View.VISIBLE);
                        }else{
                            setOngoingView();
                        }

                        if (futureTravel == null) {
                            binding.homeCardOther.setVisibility(View.GONE);

                            if (doneTravel != null) {
                                binding.homeCardOther.setVisibility(View.VISIBLE);
                                futureTravel = doneTravel;
                                setPastView();
                            } else{
                                binding.homeLayoutOther.setVisibility(View.GONE);
                            }


                        }else{
                            setFutureView();
                        }



                        binding.seeAll.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
                            navController.navigate(val);
                        });

                        binding.homeOngoingButton.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity(onGoingTravel);
                            navController.navigate(val);
                        });

                        binding.homeCardOther.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity(futureTravel);
                            navController.navigate(val);
                        });

                    } else {
                        binding.homeCardNoTravel.setVisibility(View.VISIBLE);
                    }
                });

        //travelsResponse = getTravelsResponseWithGSon(); //TODO: Cambiare e mettere il get dal repository

    }

    private void setOngoingView() {
        Travels onGoingTravel = travelsResponse.getOnGoingTravel();

        RecyclerView.LayoutManager layoutManagerRunning =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        UsersRecyclerAdapter travelRecyclerAdapterRunning = new UsersRecyclerAdapter(onGoingTravel.getMembers(), 2);
        friendsRecyclerView.setLayoutManager(layoutManagerRunning);
        friendsRecyclerView.setAdapter(travelRecyclerAdapterRunning);

//        List<TravelSegment> destinations = onGoingTravel.getDestinations();
//        TravelSegmentRecyclerAdapter travelSegmentRecyclerAdapterRunning = new TravelSegmentRecyclerAdapter(onGoingTravel.getDestinations());
//        travelSegmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        travelSegmentsRecyclerView.setAdapter(travelSegmentRecyclerAdapterRunning);

        long currentTime = System.currentTimeMillis();

        binding.cardViewStatus.setVisibility(View.VISIBLE);

        if(onGoingTravel.getStartDate().getTime() <= currentTime && onGoingTravel.getEndDate().getTime() >= currentTime){
            binding.textViewStatus.setText("In corso");
            binding.textViewStatus.setTextColor(Color.parseColor("#BCECAF"));
            binding.cardViewStatus.getBackground().setTint(Color.parseColor("#2A2FFF00"));
            binding.imageViewStatus.setColorFilter(Color.parseColor("#BCECAF"));
        } else {
            binding.textViewStatus.setText("In programma");
            binding.textViewStatus.setTextColor(Color.parseColor("#BCECAF"));
            binding.cardViewStatus.getBackground().setTint(Color.parseColor("#7A8959DF"));
            binding.imageViewStatus.setColorFilter(Color.parseColor("#BCECAF"));
        }

        binding.homeOngoingTitle.setText(onGoingTravel.getTitle());
        binding.homeOngoingLocation.setText(onGoingTravel.getDestinations().get(0).getLocation());
        binding.homeOngoingDates.setText(
                new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(onGoingTravel.getStartDate()) + " - " +
                        new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                                .format(onGoingTravel.getEndDate())
        );

        binding.homeOngoingNsegremts.setText(onGoingTravel.getDestinations().size() + " destinazioni");

        //binding.homeOngoingDescription.setText(onGoingTravel.getDescription());
    }

    private void setFutureView() {
        Travels futureTravel = travelsResponse.getFutureTravel();

        binding.homeOtherTitle.setText(futureTravel.getTitle());
        binding.homeOtherDate.setText(
                new SimpleDateFormat("dd/MM", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(futureTravel.getStartDate())
        );
        binding.homeOtherDestinations.setText(futureTravel.getDestinations().size() + " luoghi");

    }

    private void setPastView() {
        Travels pastTravel = travelsResponse.getDoneTravel();

        binding.homeOtherTitle.setText(pastTravel.getTitle());
        binding.homeOtherDate.setText(
                new SimpleDateFormat("dd/MM", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(pastTravel.getEndDate())
        );
        binding.homeOtherDestinations.setText(pastTravel.getDestinations().size() + " luoghi");

    }
    private TravelsResponse getTravelsResponseWithGSon() {
        JSONParserUtil jsonParserUtil = new JSONParserUtil(requireActivity().getApplication());
        try {
            return jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}