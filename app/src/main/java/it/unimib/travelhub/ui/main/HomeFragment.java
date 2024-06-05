package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentHomeBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;
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

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        travelsViewModel.getTravels(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        travelsResponse = ((Result.TravelsResponseSuccess) result).getData();
                        Travels onGoingTravel = travelsResponse.getOnGoingTravel();
                        Travels futureTravel = travelsResponse.getFutureTravel();
                        Travels doneTravel = travelsResponse.getDoneTravel();

                        if (travelsResponse != null) {
                            if (doneTravel == null && onGoingTravel == null) {
                                binding.homeLayoutNoTravels.setVisibility(View.VISIBLE);
                                binding.homeLayoutStandard.setVisibility(View.GONE);
                            } else if (doneTravel != null && onGoingTravel == null) {
                                binding.homeLayoutNoFutureTravels.setVisibility(View.VISIBLE);
                                binding.homeTextOngoing.setVisibility(View.GONE);
                                binding.homeCardOngoing.setVisibility(View.GONE);
                                binding.homeTextFuture.setVisibility(View.GONE);
                                binding.homeCardFuture.setVisibility(View.GONE);
                            } else if (futureTravel == null || onGoingTravel == futureTravel) {
                                binding.homeTextFuture.setVisibility(View.GONE);
                                binding.homeCardFuture.setVisibility(View.GONE);

                                setOngoingView();
                            } else {
                                setOngoingView();
                                setFutureView();
                            }
                        } else {
                            Log.e(TAG, "TravelsResponse is null");
                            binding.homeLayoutNoTravels.setVisibility(View.VISIBLE);
                            binding.homeLayoutStandard.setVisibility(View.GONE);
                        }

                        binding.seeAll.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
                            navController.navigate(val);
                        });

                        binding.homeCardOngoing.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity(onGoingTravel);
                            navController.navigate(val);
                        });

                        binding.homeCardFuture.setOnClickListener(v -> {
                            NavController navController = Navigation.findNavController(view);
                            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity(futureTravel);
                            navController.navigate(val);
                        });

                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                ((Result.Error) result).getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

        //travelsResponse = getTravelsResponseWithGSon(); //TODO: Cambiare e mettere il get dal repository

//        if (travelsResponse != null) {
//            if (travelsResponse.getDoneTravel() == null && travelsResponse.getOnGoingTravel() == null) {
//                binding.homeLayoutNoTravels.setVisibility(View.VISIBLE);
//                binding.homeLayoutStandard.setVisibility(View.GONE);
//            } else if (travelsResponse.getDoneTravel() != null && travelsResponse.getOnGoingTravel() == null) {
//                binding.homeLayoutNoFutureTravels.setVisibility(View.VISIBLE);
//                binding.homeTextOngoing.setVisibility(View.GONE);
//                binding.homeCardOngoing.setVisibility(View.GONE);
//                binding.homeTextFuture.setVisibility(View.GONE);
//                binding.homeCardFuture.setVisibility(View.GONE);
//            } else if (travelsResponse.getOnGoingTravel() != null && travelsResponse.getFutureTravel() == null ||
//                        travelsResponse.getOnGoingTravel() == travelsResponse.getFutureTravel()) {
//                binding.homeTextFuture.setVisibility(View.GONE);
//                binding.homeCardFuture.setVisibility(View.GONE);
//
//                setOngoingView();
//            } else {
//                setOngoingView();
//                setFutureView();
//            }
//        } else {
//            Log.e(TAG, "TravelsResponse is null");
//            binding.homeLayoutNoTravels.setVisibility(View.VISIBLE);
//            binding.homeLayoutStandard.setVisibility(View.GONE);
//        }
//
//        binding.seeAll.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(view);
//            NavDirections val = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
//            navController.navigate(val);
//        });
//
//        binding.homeCardOngoing.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(view);
//            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity();
//            navController.navigate(val);
//        });
//
//        binding.homeCardFuture.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(view);
//            NavDirections val = HomeFragmentDirections.actionHomeFragmentToTravelActivity();
//            navController.navigate(val);
//        });

        //travelsViewModel.addTravel(TRAVEL);



    }

    private void setOngoingView() {
        Travels onGoingTravel = travelsResponse.getOnGoingTravel();

        binding.homeOngoingTitle.setText(onGoingTravel.getTitle());
        binding.homeOngoingStartDate.setText(
                new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(onGoingTravel.getStartDate())
        );
        binding.homeOngoingEndDate.setText(
                new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(onGoingTravel.getEndDate())
        );
        binding.homeOngoingDescription.setText(onGoingTravel.getDescription());
    }

    private void setFutureView() {
        Travels futureTravel = travelsResponse.getFutureTravel();

        binding.homeFutureTitle.setText(futureTravel.getTitle());
        binding.homeFutureStartDate.setText(
                new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(futureTravel.getStartDate())
        );
        binding.homeFutureEndDate.setText(
                new SimpleDateFormat("dd/MM/yyyy", requireActivity().getResources().getConfiguration().getLocales().get(0))
                        .format(futureTravel.getEndDate())
        );
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