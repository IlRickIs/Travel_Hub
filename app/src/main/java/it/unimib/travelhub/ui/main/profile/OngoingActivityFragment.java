package it.unimib.travelhub.ui.main.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentOngoingActivityBinding;
public class OngoingActivityFragment extends Fragment {
    private FragmentOngoingActivityBinding binding;
    private AddRecyclerAdapter addRecyclerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public OngoingActivityFragment() {
        // Required empty public constructor
    }
    public static OngoingActivityFragment newInstance() {
        return new OngoingActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOngoingActivityBinding.inflate(inflater, container, false);
        String[] localDataset = {"activity 1", "activity 2", "activity 3", "activity 4", "activity 8", "activity 9", "activity 10"};
        mLayoutManager = new LinearLayoutManager(getActivity());
        addRecyclerAdapter = new AddRecyclerAdapter(localDataset);
        binding.ongoingActivitiesRecyclerView.setLayoutManager(mLayoutManager);
        binding.ongoingActivitiesRecyclerView.setAdapter(addRecyclerAdapter);
        return binding.getRoot();
    }
}