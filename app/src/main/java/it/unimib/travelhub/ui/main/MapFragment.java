package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentMapBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.TravelsResponse;
import it.unimib.travelhub.ui.profile.ProfileFragmentAdapter;
import it.unimib.travelhub.ui.travels.TravelsViewModel;
import it.unimib.travelhub.ui.travels.TravelsViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gmap;
    private FragmentMapBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TravelsResponse travelsResponse;
    private TravelsViewModel travelsViewModel;

    private ListView listView;
    private ArrayAdapter<Travels> arrayAdapter;
    private ArrayList<Travels> travelsList;

    public MapFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);

        SupportMapFragment supportMapFragment;
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .zoomGesturesEnabled(true);
        supportMapFragment = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().replace(R.id.google_map, supportMapFragment).commit();


        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        travelsViewModel.getTravels(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        travelsResponse = ((Result.TravelsResponseSuccess) result).getData();
                        travelsList = (ArrayList<Travels>) travelsResponse.getTravelsList();
                            supportMapFragment.getMapAsync(googleMap -> {
                                // When map is loaded
                                for(Travels travel : travelsList){
                                    for(TravelSegment segment : travel.getDestinations()){
                                        if (segment.getLat() != 0 && segment.getLng() != 0){
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(segment.getLat(), segment.getLng()))
                                                    .title(segment.getLocation())
                                                    .snippet(travel.getTitle()));

                                        }

                                    }
                                }
                            });
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
    }


}