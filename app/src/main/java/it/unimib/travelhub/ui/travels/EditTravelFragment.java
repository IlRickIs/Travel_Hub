package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.DESTINATIONS_HINTS;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_TEXTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_HINTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_TEXTS;
import static it.unimib.travelhub.util.Constants.TRAVEL_DESCRIPTION;
import static it.unimib.travelhub.util.Constants.TRAVEL_TITLE;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TextBoxesRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentEditTravelBinding;


public class EditTravelFragment extends Fragment {

    private FragmentEditTravelBinding binding;

    private TextBoxesRecyclerAdapter textBoxesRecyclerAdapter;

    private TextBoxesRecyclerAdapter friendTextBoxesRecyclerAdapter;
    private static final String TAG = EditTravelFragment.class.getSimpleName();
    final Calendar myCalendar= Calendar.getInstance();
    private List<String> friendTextList;
    private List<String> destinationsText;
    private List<String> hintsList;
    private List<String> friendHintsList;
    public EditTravelFragment() {
    }

    public static EditTravelFragment newInstance(String param1, String param2) {
        EditTravelFragment fragment = new EditTravelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            destinationsText = savedInstanceState.getStringArrayList(DESTINATIONS_TEXTS);
            friendTextList = savedInstanceState.getStringArrayList(FRIENDS_TEXTS);
            hintsList = savedInstanceState.getStringArrayList(DESTINATIONS_HINTS);
            friendHintsList = savedInstanceState.getStringArrayList(FRIENDS_HINTS);
            binding.titleFormEditText.setText(savedInstanceState.getString(TRAVEL_TITLE));
            binding.descriptionFormEditText.setText(savedInstanceState.getString(TRAVEL_DESCRIPTION));
        } else {
            destinationsText = new ArrayList<>();
            friendTextList = new ArrayList<>();
            hintsList = new ArrayList<>();
            friendHintsList = new ArrayList<>();
        }
    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTravelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatePickerDialog.OnDateSetListener date = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        };
        binding.editTxtFromForm.setOnClickListener(v ->
        {
            new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            updateLabel(binding.editTxtFromForm);
        });

        binding.editTxtToForm.setOnClickListener(v ->
        {
            new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            updateLabel(binding.editTxtToForm);
        });

        textBoxesRecyclerAdapter = new TextBoxesRecyclerAdapter(hintsList, destinationsText,new TextBoxesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int actualItems = textBoxesRecyclerAdapter.getItemCount();
                if (actualItems > 0){
                    removeItem(textBoxesRecyclerAdapter,position);
                }
            }
            @Override
            public void onKeyPressed(int position, String text) {
                textBoxesRecyclerAdapter.getDestinationsTexts().set(position, text);
                destinationsText = textBoxesRecyclerAdapter.getDestinationsTexts(); // Update the list immediately
            }
        });

        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        binding.recyclerDestinations.setLayoutManager(mLayoutManager);
        binding.recyclerDestinations.setAdapter(textBoxesRecyclerAdapter);

        binding.addDestinationButton.setOnClickListener(v -> {
            updateItem(textBoxesRecyclerAdapter, R.string.destination);
        });
        friendTextBoxesRecyclerAdapter = new TextBoxesRecyclerAdapter(friendHintsList, friendTextList, new TextBoxesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int actualItems = friendTextBoxesRecyclerAdapter.getItemCount();
                if (actualItems > 0){
                    removeItem(friendTextBoxesRecyclerAdapter,position);
                }
            }
            @Override
            public void onKeyPressed(int position, String text) {
                friendTextBoxesRecyclerAdapter.getDestinationsTexts().set(position, text);
                friendTextList = friendTextBoxesRecyclerAdapter.getDestinationsTexts(); // Update the list immediately
            }
        });
        LinearLayoutManager friendLayoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        binding.recyclerFriends.setLayoutManager(friendLayoutManager);
        binding.recyclerFriends.setAdapter(friendTextBoxesRecyclerAdapter);

        binding.addFriendButton.setOnClickListener(v -> {
            updateItem(friendTextBoxesRecyclerAdapter, R.string.add_friends_email);
        });

    }

private void updateItem(TextBoxesRecyclerAdapter adapter, int id){
    adapter.getTextBoxesHints().add(getString(id));
    adapter.getDestinationsTexts().add("");
    adapter.notifyDataSetChanged();
}
private void removeItem(TextBoxesRecyclerAdapter adapter, int position) {
    adapter.getTextBoxesHints().remove(position);
    adapter.getDestinationsTexts().remove(position);
    adapter.notifyDataSetChanged();
}
    private void printdataset(List<String> dataset) {
        for (String s : dataset) {
           Log.d(TAG, "printdataset: " + s);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(DESTINATIONS_TEXTS, (ArrayList<String>) destinationsText);
        outState.putStringArrayList(FRIENDS_TEXTS, (ArrayList<String>) friendTextList);
        outState.putStringArrayList(DESTINATIONS_HINTS, (ArrayList<String>) hintsList);
        outState.putStringArrayList(FRIENDS_HINTS, (ArrayList<String>) friendHintsList);
        outState.putString(TRAVEL_TITLE, binding.titleFormEditText.getText().toString());
        outState.putString(TRAVEL_DESCRIPTION, binding.descriptionFormEditText.getText().toString());
    }
}