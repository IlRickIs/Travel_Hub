package it.unimib.travelhub.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.AddRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentAddBinding;
import it.unimib.travelhub.ui.travels.AddTravelActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {
    private FragmentAddBinding binding;
    private AddRecyclerAdapter addRecyclerAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = AddFragment.class.getSimpleName();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        String[] localDataset = {"activity 1", "activity 2", "activity 3"};
        mLayoutManager = new LinearLayoutManager(getActivity());

        addRecyclerAdapter = new AddRecyclerAdapter(localDataset, new AddRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String[] attualDataset = addRecyclerAdapter.getDataSet();
                Log.d(TAG, "onItemClick: " +  attualDataset.toString());
                if (position == 0) {
                    String[] newDataset = new String[attualDataset.length + 1];
                    for(int i = 0; i < attualDataset.length; i++) {
                        newDataset[i] = attualDataset[i];
                    }
                    newDataset[attualDataset.length] = "activity " + (attualDataset.length + 1);
                    addRecyclerAdapter.setDataSet(newDataset);
                }
            }
        });
        binding.ongoingActivitiesRecyclerView.setLayoutManager(mLayoutManager);
        binding.ongoingActivitiesRecyclerView.setAdapter(addRecyclerAdapter);
        binding.buttonAddActivity.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), AddTravelActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
