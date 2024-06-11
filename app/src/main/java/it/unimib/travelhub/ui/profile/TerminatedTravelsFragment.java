package it.unimib.travelhub.ui.profile;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentTerminatedTravelsBinding;
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
 * Use the {@link TerminatedTravelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TerminatedTravelsFragment extends Fragment {
    private FragmentTerminatedTravelsBinding binding;
    protected RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TravelsResponse travelsResponse;
    private TravelsViewModel travelsViewModel;
    public TerminatedTravelsFragment() {
        // Required empty public constructor
    }
    public static TerminatedTravelsFragment newInstance() {
        TerminatedTravelsFragment fragment = new TerminatedTravelsFragment();
        return fragment;
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

        binding = FragmentTerminatedTravelsBinding.inflate(inflater, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        return binding.getRoot();
    }

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

        RecyclerView travelRecyclerViewRunning = binding.terminatedActivitiesRecyclerView;

        //List<Travels> runningTravelsList = getDoneTravelsListWithGSon();

        List<Travels> doneTravelList = new ArrayList<Travels>();

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }
         travelsViewModel.getTravels(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        travelsResponse = ((Result.TravelsResponseSuccess) result).getData();
                        doneTravelList.addAll(travelsResponse.getDoneTravelsList());
                        RecyclerView.LayoutManager layoutManagerRunning =
                                new LinearLayoutManager(requireContext(),
                                        LinearLayoutManager.VERTICAL, false);

                        TravelRecyclerAdapter travelRecyclerAdapterRunning = new TravelRecyclerAdapter(doneTravelList,
                                new TravelRecyclerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onTravelsItemClick(Travels travels) {
                                        Navigation.findNavController(requireView())
                                                .navigate(ProfileFragmentDirections.actionProfileFragmentToTravelActivity(travels));
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

    private List<Travels> getDoneTravelsListWithGSon() { //TODO: Cambiare e mettere il get dal repository
        JSONParserUtil jsonParserUtil = new JSONParserUtil(requireActivity().getApplication());
        try {
            return jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE)
                    .getDoneTravelsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}