package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.TRAVEL_ADDED;
import static it.unimib.travelhub.util.Constants.TRAVEL_DELETED;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.ActivityTravelBinding;
import it.unimib.travelhub.R;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.util.ServiceLocator;

public class TravelActivity extends AppCompatActivity {

    private TravelsViewModel travelsViewModel;
    private ActivityTravelBinding binding;

    private Travels travel;
    final Calendar myCalendar= Calendar.getInstance();

    private TravelFragmentAdapter myFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTravelBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ITravelsRepository travelsRepository =
                ServiceLocator.getInstance().getTravelsRepository(
                        TravelActivity.this.getApplication()
                );
        if (travelsRepository != null) {
            // This is the way to create a ViewModel with custom parameters
            // (see NewsViewModelFactory class for the implementation details)
            travelsViewModel = new ViewModelProvider(
                    TravelActivity.this,
                    new TravelsViewModelFactory(travelsRepository)).get(TravelsViewModel.class);
        } else {
            Snackbar.make(TravelActivity.this.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPagerTravel.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.viewPagerTravel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

        TravelActivityArgs args = TravelActivityArgs.fromBundle(getIntent().getExtras());
        travel = args.getTravel();

        FragmentManager fragmentManager = getSupportFragmentManager();
        myFragmentAdapter = new TravelFragmentAdapter(fragmentManager, getLifecycle(), travel);
        binding.viewPagerTravel.setAdapter(myFragmentAdapter);

        binding.travelTitle.setText(travel.getTitle());
        binding.buttonBack.setOnClickListener(v -> finish());
        String startDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getStartDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0))
                .format(travel.getEndDate());
        binding.travelStartDate.setText(startDate);
        binding.travelEndDate.setText(endDate);

        FrameLayout standardBottomSheet = findViewById(R.id.standard_bottom_sheet);
        BottomSheetBehavior<View> standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet);
        binding.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TravelActivity", "Button more clicked"+ travel);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TravelActivity.this);
                View view1 = LayoutInflater.from(TravelActivity.this).inflate(R.layout.bottom_sheet_layout_travel, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton buttonDelete = view1.findViewById(R.id.button_delete);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TravelActivity.this);
                        builder1.setMessage("Sei sicuro di voler cancellare il viaggio?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                (dialog, id) -> {
                                    deleteTravel(travel);
                                    Intent intent = new Intent(TravelActivity.this, MainActivity.class);
                                    intent.putExtra(TRAVEL_DELETED, true);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                });

                        builder1.setNegativeButton(
                                "No",
                                (dialog, id) -> dialog.cancel());

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }
                });
            }
        });
        editTravel();
    }


    private void editTravel() {

        TextInputEditText travelTitle = findViewById(R.id.travel_title);
        EditText travelStartDate = findViewById(R.id.travel_start_date);
        EditText travelEndDate = findViewById(R.id.travel_end_date);

        travelTitle.setOnClickListener(v -> {
            travelTitle.setFocusableInTouchMode(true);
            travelTitle.setFocusable(true);
            travelTitle.requestFocus();
            travelTitle.setOnFocusChangeListener((v1, hasFocus) -> {
                if (!hasFocus) {
                    travelTitle.setFocusable(false);
                }
            });
        });
        travelTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travel.setTitle(s.toString());
                showEditButton();
            }
            @Override
            public void afterTextChanged(Editable s) {
                travelTitle.setFocusable(false);
            }
        });

        DatePickerDialog.OnDateSetListener date1 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            String date = updateLabel(binding.travelStartDate);
            travel.setStartDate(parseStringToDate(date + " 00:00:00"));
        };

        DatePickerDialog.OnDateSetListener date2 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            String date = updateLabel(binding.travelEndDate);
            travel.setEndDate(parseStringToDate(date + " 23:59:59"));
        };

        travelStartDate.setOnClickListener(v ->
        {
            new DatePickerDialog(this, date1 ,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            showEditButton();
        });

        travelEndDate.setOnClickListener(v ->
        {
            new DatePickerDialog(this, date2,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            showEditButton();
        });

    }

    private void deleteTravel(Travels travel) {

        Observer<Result> resultObserver = new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result != null && result.isSuccess()) {
                    Result.TravelsResponseSuccess travelResponse = (Result.TravelsResponseSuccess) result;
                    Travels travelDeleted = travelResponse.getData().getTravelsList().get(0);
                    Log.d("CommunityFragment", "Travel deleted: " + travelDeleted);
                } else {
                    Result.Error error = (Result.Error) result;
                    Log.d("CommunityFragment", "Travel not deleted, Error: " + error.getMessage());
                }
                travelsViewModel.deleteTravel(travel).removeObserver(this);
            }
        };

        travelsViewModel.deleteTravel(travel).observe(this, resultObserver);

    }
    public void showEditButton(){
        binding.buttonEdit.setVisibility(View.VISIBLE);
        binding.buttonEdit.setOnClickListener(v -> {
            updateTravel(travel); //TODO manage edit button
        });
    }
    private Date parseStringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", getResources().getConfiguration().getLocales().get(0));
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }
    private String updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, getResources().getConfiguration().getLocales().get(0));
        String s = sdf.format(myCalendar.getTime());
        editText.setText(s);
        return s;
    }

    private boolean updateTravel(Travels travel) {
        //TODO update travel
        return false;
    }
}