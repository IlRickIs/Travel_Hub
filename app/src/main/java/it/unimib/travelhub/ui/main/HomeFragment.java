package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
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

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TravelRecyclerAdapter;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.util.JSONParserUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
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

        List<Travels> runningTravelsList = getRunningTravelsListWithGSon();
        List<Travels> doneTravelsList = getDoneTravelsListWithGSon();

        RecyclerView homeRecyclerViewRunning = view.findViewById(R.id.home_recyclerview_running);

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

        homeRecyclerViewRunning.setLayoutManager(layoutManagerRunning);
        homeRecyclerViewRunning.setAdapter(travelRecyclerAdapterRunning);

        RecyclerView homeRecyclerViewDone = view.findViewById(R.id.home_recyclerview_done);

        RecyclerView.LayoutManager layoutManagerDone =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        TravelRecyclerAdapter travelRecyclerAdapterDone = new TravelRecyclerAdapter(doneTravelsList,
                new TravelRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onTravelsItemClick(Travels travels) {
                        Snackbar.make(view, travels.getTitle(), Snackbar.LENGTH_SHORT).show();
                    }
                });

        homeRecyclerViewDone.setLayoutManager(layoutManagerDone);
        homeRecyclerViewDone.setAdapter(travelRecyclerAdapterDone);

    }

    /**
     * Returns the list of News using Gson.
     * @return The list of News.
     */
    private List<Travels> getRunningTravelsListWithGSon() {
        JSONParserUtil jsonParserUtil = new JSONParserUtil(requireActivity().getApplication());
        try {
            return jsonParserUtil.parseJSONFileWithGSon(TRAVELS_TEST_JSON_FILE)
                    .getRunningTravelsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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