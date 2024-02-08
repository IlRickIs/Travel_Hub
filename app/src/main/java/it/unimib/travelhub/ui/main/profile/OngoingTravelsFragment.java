package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentOngoingTravelsBinding;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.util.JSONParserUtil;

public class OngoingTravelsFragment extends Fragment {
    private FragmentOngoingTravelsBinding binding;
    private AddRecyclerAdapter addRecyclerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public OngoingTravelsFragment() {
        // Required empty public constructor
    }
    public static OngoingTravelsFragment newInstance() {
        return new OngoingTravelsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOngoingTravelsBinding.inflate(inflater, container, false);
        String[] localDataset = {"activity 1", "activity 2", "activity 3", "activity 4", "activity 8", "activity 9", "activity 10"};
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

        RecyclerView travelRecyclerViewRunning = binding.ongoingActivitiesRecyclerView;

        List<Travels> runningTravelsList = getRunningTravelsListWithGSon();

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
    }

    private List<Travels> getRunningTravelsListWithGSon() {
        JSONParserUtil jsonParserUtil = new JSONParserUtil(requireActivity().getApplication());
        try {
            return jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE)
                    .getFutureTravelsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}