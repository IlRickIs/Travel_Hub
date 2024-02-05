package it.unimib.travelhub.ui.edit;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import it.unimib.travelhub.adapter.TextBoxesRecyclerAdapter;
import it.unimib.travelhub.databinding.FragmentEditBinding;


public class EditFragment extends Fragment {

    private FragmentEditBinding binding;

    private TextBoxesRecyclerAdapter textBoxesRecyclerAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = EditFragment.class.getSimpleName();
    final Calendar myCalendar= Calendar.getInstance();
    public EditFragment() {
    }

    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getContext());

    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
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

        List<String> hintsList = new ArrayList<>();
        List<String> destinationsText = new ArrayList<>();
        textBoxesRecyclerAdapter = new TextBoxesRecyclerAdapter(hintsList, destinationsText,new TextBoxesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int actualItems = textBoxesRecyclerAdapter.getItemCount();
                if (actualItems > 0){
                    textBoxesRecyclerAdapter.getDestinationsHints().remove(position);
                    textBoxesRecyclerAdapter.getDestinationsTexts().remove(position);
                    textBoxesRecyclerAdapter.notifyDataSetChanged();
                    printdataset(textBoxesRecyclerAdapter.getDestinationsHints());
                    printdataset(textBoxesRecyclerAdapter.getDestinationsTexts());
                }
            }

            @Override
            public void onKeyPressed(int position, String text) {
                textBoxesRecyclerAdapter.getDestinationsTexts().set(position, text);
            }
        });

        binding.destinationsTextBoxes.setLayoutManager(mLayoutManager);
        binding.destinationsTextBoxes.setAdapter(textBoxesRecyclerAdapter);

        binding.addDestinationButton.setOnClickListener(v -> {
            int actualItems = textBoxesRecyclerAdapter.getItemCount();
            textBoxesRecyclerAdapter.getDestinationsHints().add("Destination" + (actualItems + 1));
            textBoxesRecyclerAdapter.getDestinationsTexts().add("");
            textBoxesRecyclerAdapter.notifyDataSetChanged();
        });

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
}