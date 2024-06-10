package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.TRAVEL_ADDED;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.FragmentEditTravelSegmentBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.util.ServiceLocator;

public class NewTravelSegment extends Fragment {

    private FragmentEditTravelSegmentBinding binding;
    private Travels travel;

    private TravelsViewModel travelsViewModel;

    final Calendar myCalendar= Calendar.getInstance();
    private String destinationName;
    private String dateFrom;
    private String dateTo;

    Button saveTravelBtn;
    private String description;

    private static final String TAG = NewTravelSegment.class.getSimpleName();

    public NewTravelSegment() {
        // Required empty public constructor
    }

    public static NewTravelSegment newInstance() {
        NewTravelSegment fragment = new NewTravelSegment();
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

        ITravelsRepository travelsRepository =
                ServiceLocator.getInstance().getTravelsRepository(
                        requireActivity().getApplication()
                );

        if (travelsRepository != null) {
            travelsViewModel = new ViewModelProvider(
                    requireActivity(),
                    new TravelsViewModelFactory(travelsRepository)).get(TravelsViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditTravelSegmentBinding.inflate(inflater, container, false);

        Log.d("EditTravelSegment", "onCreate: " + travel);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        Date toDate = travel.getDestinations().get(travel.getDestinations().size() - 1).getDateTo();
        if (toDate != null)
            binding.fromEditText.setText(sdf.format(toDate));

        binding.addDestinationButtonSegment.setOnClickListener(v -> {
            if(checkNulls()){
                return;
            }
            Bundle bundle = new Bundle();
            travel.getDestinations().add(buildTravelSegment());
            bundle.putSerializable("travel", travel);

            NewTravelSegment newTravelSegment = new NewTravelSegment();
            newTravelSegment.setArguments(bundle);
            // change fragment and pass the data
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activityAddFragmentContainerView, newTravelSegment);
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
            travel.getDestinations().get(0).setDateTo(null);
            travel.getDestinations().add(buildTravelSegment());
            Log.d(TAG, "TRAVEL TO CREATE: " + travel);
            //add the travel
            //travelsViewModel.addTravel(travel);
            //attachTravelObserver();

        });
    }

    private TravelSegment buildTravelSegment() {
        TravelSegment travelSegment = new TravelSegment();
        travelSegment.setLocation(binding.destinationEditText.getText().toString());
        travelSegment.setDescription(binding.descriptionEditText.getText().toString());
        String dateFrom = binding.fromEditText.getText().toString() + " 00:00:00";
        String dateTo = binding.toEditText.getText().toString() + " 23:59:59";
        //TODO: what if the date is empty?
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
        Date dateFrom = null;
        Date dateTo = null;
        if (!binding.fromEditText.getText().toString().isEmpty())
            dateFrom = parseStringToDate(binding.fromEditText.getText().toString() + " 00:00:00");
        Date startDate = travel.getStartDate();
        if (!binding.toEditText.getText().toString().isEmpty())
            dateTo = parseStringToDate(binding.toEditText.getText().toString() + " 23:59:59");
        Date endDate = travel.getEndDate();
        if (dateFrom != null) {
            if (dateFrom.before(startDate)) {
                binding.fromEditText.setError(getString(R.string.date_under_limit));
                isNull = true;
            }else if(dateFrom.after(endDate)){
                binding.fromEditText.setError(getString(R.string.date_over_limit));
                isNull = true;
            }else{
                binding.fromEditText.setError(null);
            }
        }
        if (dateTo != null) {
            if (dateTo.before(startDate)) {
                binding.toEditText.setError(getString(R.string.date_under_limit));
                isNull = true;
            }else if(dateTo.after(endDate)){
                binding.toEditText.setError(getString(R.string.date_over_limit));
                isNull = true;
            }else{
                binding.toEditText.setError(null);
            }
        }


        return isNull;
    }

    public Date parseStringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", getResources().getConfiguration().getLocales().get(0));
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
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, getResources().getConfiguration().getLocales().get(0));
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void attachTravelObserver(){
        travelsViewModel.getTravelAddLiveData().observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()){
                Log.d(TAG, "travel " + ((Result.TravelsResponseSuccess) result).getData().toString() + " added successfully");

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.putExtra(TRAVEL_ADDED, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();

            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        ((Result.Error)result).getMessage(),
                        Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "Error while adding travel: " + ((Result.Error)result).getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}