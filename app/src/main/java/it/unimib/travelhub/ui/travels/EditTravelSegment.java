package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.DESTINATIONS_HINTS;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_TEXTS;
import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.FRIENDS_HINTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_TEXTS;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.TRAVEL_DESCRIPTION;
import static it.unimib.travelhub.util.Constants.TRAVEL_TITLE;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.ActivityAddTravelBinding;
import it.unimib.travelhub.databinding.FragmentEditTravelBinding;
import it.unimib.travelhub.databinding.FragmentEditTravelSegmentBinding;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;

public class EditTravelSegment extends Fragment {

    private FragmentEditTravelSegmentBinding binding;
    private Travels travel;

    final Calendar myCalendar= Calendar.getInstance();
    private String destinationName;
    private String dateFrom;
    private String dateTo;

    Button saveTravelBtn;
    private String description;

    private static final String TAG = EditTravelSegment.class.getSimpleName();

    public EditTravelSegment() {
        // Required empty public constructor
    }

    public static EditTravelSegment newInstance() {
        EditTravelSegment fragment = new EditTravelSegment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travel = (Travels) getArguments().getSerializable("travel");
        } else {
            Log.d("EditTravelSegment", "onCreate: no arguments");
        }
        saveTravelBtn = requireActivity().findViewById(R.id.button_save_activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditTravelSegmentBinding.inflate(inflater, container, false);

        Log.d("EditTravelSegment", "onCreate: " + travel);

        binding.addDestinationButtonSegment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            travel.getDestinations().add(buildTravelSegment());
            bundle.putSerializable("travel", travel);

            EditTravelSegment editTravelSegment = new EditTravelSegment();
            editTravelSegment.setArguments(bundle);
            // change fragment and pass the data
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activityAddFragmentContainerView, editTravelSegment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        saveTravelBtn.setClickable(true);
       //make it visible appear clikable
        saveTravelBtn.setAlpha(1f);



        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
                builder1.setMessage("Are you sure you want to come back? Your last destination will be lost.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            dialog.cancel();
                            travel.getDestinations().remove(travel.getDestinations().size() - 1);
                            getParentFragmentManager().popBackStack();
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatePickerDialog.OnDateSetListener date1 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(binding.fromEditText);
        };
        DatePickerDialog.OnDateSetListener date2 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(binding.toEditText);
        };
        binding.fromEditText.setOnClickListener(v ->
        {
            new DatePickerDialog(getContext(), date1 ,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.toEditText.setOnClickListener(v ->
        {
            new DatePickerDialog(getContext(),date2,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        saveTravelBtn.setOnClickListener(v -> {
            if(checkNulls()){
                return;
            }
            travel.getDestinations().add(buildTravelSegment());
            Log.d(TAG, "TRAVEL TO CREATE: " + travel);
            //add the travel
        });
    }

    private TravelSegment buildTravelSegment() {
        TravelSegment travelSegment = new TravelSegment();
        travelSegment.setLocation(binding.destinationEditText.getText().toString());
        travelSegment.setDescription(binding.descriptionEditText.getText().toString());
        String dateFrom = binding.fromEditText.getText().toString();
        String dateTo = binding.toEditText.getText().toString();
        travelSegment.setDateFrom(parseStringToDate(dateFrom));
        travelSegment.setDateTo(parseStringToDate(dateTo));
        return travelSegment;
    }

    private boolean checkNulls(){
        boolean isNull = false;
        if (binding.destinationEditText.getText().toString().isEmpty()) {
            binding.destinationEditText.setError(getString(R.string.destination_error));
            isNull = true;
        }else{
            binding.destinationEditText.setError(null);
        }
//        if (binding.fromEditText.getText().toString().isEmpty()) {
//            binding.fromEditText.setError(getString(R.string.date_empty_error));
//            isNull = true;
//        }else{
//            binding.fromEditText.setError(null);
//        }
//        if (binding.toEditText.getText().toString().isEmpty()) {
//            binding.toEditText.setError(getString(R.string.date_empty_error));
//            isNull = true;
//        }else{
//            binding.toEditText.setError(null);
//        }

        return isNull;
    }

    public Date parseStringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ITALY);
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}