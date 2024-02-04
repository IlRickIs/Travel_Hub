package it.unimib.travelhub.ui.main.profile;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentTerminatedActivityBinding;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.util.JSONParserUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TerminatedActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TerminatedActivityFragment extends Fragment {
    private FragmentTerminatedActivityBinding binding;
    public TerminatedActivityFragment() {
        // Required empty public constructor
    }
    public static TerminatedActivityFragment newInstance() {
        TerminatedActivityFragment fragment = new TerminatedActivityFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTerminatedActivityBinding.inflate(inflater, container, false);
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

        List<Travels> runningTravelsList = getDoneTravelsListWithGSon();

        RecyclerView.LayoutManager layoutManagerRunning =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        TravelRecyclerAdapter travelRecyclerAdapterRunning = new TravelRecyclerAdapter(runningTravelsList,
                new TravelRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onTravelsItemClick(Travels travels) {
                        Snackbar.make(view, travels.getTitle(), Snackbar.LENGTH_SHORT).show();
                    }
                });

        travelRecyclerViewRunning.setLayoutManager(layoutManagerRunning);
        travelRecyclerViewRunning.setAdapter(travelRecyclerAdapterRunning);
    }

    private List<Travels> getDoneTravelsListWithGSon() {
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