package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.TRAVELS_TEST_JSON_FILE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.FragmentHomeBinding;
import it.unimib.travelhub.model.TravelsResponse;
import it.unimib.travelhub.util.JSONParserUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private FragmentHomeBinding binding;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        TravelsResponse travelsResponse = getTravelsResponseWithGSon();

        assert travelsResponse != null; //TODO: check if the response is null
        if (travelsResponse.getDoneTravel() == null && travelsResponse.getOnGoingTravel() == null) {
            binding.homeLayoutNoTravels.setVisibility(View.VISIBLE);
            binding.homeLayoutStandard.setVisibility(View.GONE);
        } else if (travelsResponse.getDoneTravel() != null && travelsResponse.getOnGoingTravel() == null) {
            binding.homeLayoutNoFutureTravels.setVisibility(View.VISIBLE);
            binding.homeTextOngoing.setVisibility(View.GONE);
            binding.homeCardOngoing.setVisibility(View.GONE);
            binding.homeTextFuture.setVisibility(View.GONE);
            binding.homeCardFuture.setVisibility(View.GONE);
        } else if (travelsResponse.getDoneTravel() == null && travelsResponse.getOnGoingTravel() != null && travelsResponse.getFutureTravel() == null) {
            binding.homeTextFuture.setVisibility(View.GONE);
            binding.homeCardFuture.setVisibility(View.GONE);
        } else if (travelsResponse.getDoneTravel() != null && travelsResponse.getOnGoingTravel() != null && travelsResponse.getFutureTravel() == null) {
            binding.homeTextFuture.setVisibility(View.GONE);
            binding.homeCardFuture.setVisibility(View.GONE);
        }

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

        binding.seeAll.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            NavDirections val = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
            navController.navigate(val);
        });

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