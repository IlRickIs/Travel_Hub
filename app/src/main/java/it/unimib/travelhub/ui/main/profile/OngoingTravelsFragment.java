package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.ArrayList;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentOngoingTravelsBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;
import it.unimib.travelhub.ui.travels.TravelsViewModel;
import it.unimib.travelhub.ui.travels.TravelsViewModelFactory;
import it.unimib.travelhub.util.JSONParserUtil;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class OngoingTravelsFragment extends Fragment {
    private FragmentOngoingTravelsBinding binding;
    protected RecyclerView.LayoutManager mLayoutManager;

    private SharedPreferencesUtil sharedPreferencesUtil;
    private TravelsResponse travelsResponse;
    private TravelsViewModel travelsViewModel;

    public OngoingTravelsFragment() {
        // Required empty public constructor
    }
    public static OngoingTravelsFragment newInstance() {
        return new OngoingTravelsFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentOngoingTravelsBinding.inflate(inflater, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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

        //List<Travels> runningTravelsList = getOngoingTravelsListWithGSon();
        List<Travels> runningTravelsList = new ArrayList<Travels>();

        travelsViewModel.getTravels(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {

                        travelsResponse = ((Result.TravelsResponseSuccess) result).getData();
                        runningTravelsList.add(travelsResponse.getOnGoingTravel());
                        runningTravelsList.addAll(travelsResponse.getFutureTravelsList());

                        RecyclerView travelRecyclerViewRunning = binding.ongoingActivitiesRecyclerView;
                        RecyclerView.LayoutManager layoutManagerRunning =
                                new LinearLayoutManager(requireContext(),
                                        LinearLayoutManager.VERTICAL, false);
                        TravelRecyclerAdapter travelRecyclerAdapterRunning = new TravelRecyclerAdapter(runningTravelsList,
                                new TravelRecyclerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onTravelsItemClick(Travels travels) {
                                        Navigation.findNavController(requireView())
                                                .navigate(ProfileFragmentDirections.actionProfileFragmentToTravelActivity());
                                    }
                                });
                        travelRecyclerViewRunning.setLayoutManager(layoutManagerRunning);
                        travelRecyclerViewRunning.setAdapter(travelRecyclerAdapterRunning);

                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private List<Travels> getOngoingTravelsListWithGSon() {
        JSONParserUtil jsonParserUtil = new JSONParserUtil(requireActivity().getApplication());
        List<Travels> travelsList = new ArrayList<Travels>();

        try {
            travelsList.add(jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE)
                    .getOnGoingTravel());
            travelsList.addAll(jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE)
                    .getFutureTravelsList());
            return travelsList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}