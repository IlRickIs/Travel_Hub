package it.unimib.travelhub.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentTerminatedActivityBinding;

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
        String[] anotherlocalDataset = {"activity 5", "activity 6", "activity 7", "activity 8", "activity 9", "activity 10"};
        LinearLayoutManager anotherLayoutManager = new LinearLayoutManager(getActivity());
        AddRecyclerAdapter anotherAddRecyclerAdapter = new AddRecyclerAdapter(anotherlocalDataset);
        binding.terminatedActivitiesRecyclerView.setLayoutManager(anotherLayoutManager);
        binding.terminatedActivitiesRecyclerView.setAdapter(anotherAddRecyclerAdapter);
        return binding.getRoot();
    }
}