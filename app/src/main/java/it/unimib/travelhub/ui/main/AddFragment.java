package it.unimib.travelhub.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentAddBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    private AddRecyclerAdapter addRecyclerAdapter;
    private FragmentAddBinding binding;

    protected RecyclerView.LayoutManager mLayoutManager;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] localDataset = {"activity 1", "activity 2", "activity 3", "activity 4", "activity 8", "activity 9", "activity 10"};
        mLayoutManager = new LinearLayoutManager(getActivity());
        addRecyclerAdapter = new AddRecyclerAdapter(localDataset);
        binding.ongoingActivitiesRecyclerView.setLayoutManager(mLayoutManager);
        binding.ongoingActivitiesRecyclerView.setAdapter(addRecyclerAdapter);

        String[] anotherlocalDataset = {"activity 5", "activity 6", "activity 7", "activity 8", "activity 9", "activity 10"};
        LinearLayoutManager anotherLayoutManager = new LinearLayoutManager(getActivity());
        //AddRecyclerAdapter anotherAddRecyclerAdapter = new AddRecyclerAdapter(anotherlocalDataset);
        binding.terminatedActivitiesRecyclerView.setLayoutManager(anotherLayoutManager);
        binding.terminatedActivitiesRecyclerView.setAdapter(addRecyclerAdapter);

        binding.buttonAddActivity.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(AddFragmentDirections.actionAddFragmentToAddTravelActivity());
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
