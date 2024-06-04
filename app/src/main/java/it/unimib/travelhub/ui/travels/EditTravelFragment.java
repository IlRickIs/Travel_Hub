package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.DESTINATION;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_HINTS;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_TEXTS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERNAMES_COLLECTION;
import static it.unimib.travelhub.util.Constants.FRIEND;
import static it.unimib.travelhub.util.Constants.FRIENDS_HINTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_TEXTS;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.TRAVEL_DESCRIPTION;
import static it.unimib.travelhub.util.Constants.TRAVEL_TITLE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.TextBoxesRecyclerAdapter;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.databinding.FragmentEditTravelBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.TravelSegment;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.welcome.UserViewModel;
import it.unimib.travelhub.ui.welcome.UserViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.util.UserListVerification;


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
    private Activity mainActivity;
    private TravelsViewModel travelsViewModel;
    private DataEncryptionUtil dataEncryptionUtil;

    private UserViewModel userViewModel;

    AtomicBoolean exists = new AtomicBoolean(false);
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
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

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

        IUserRepository userRepository =
                ServiceLocator.getInstance().getUserRepository(
                        requireActivity().getApplication()
                );

        if (userRepository != null) {
            userViewModel = new ViewModelProvider(
                    requireActivity(),
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
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
        mainActivity = (Activity) requireActivity();

        userViewModel.getIsUserRegistered().observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()){
                User user = ((Result.UserResponseSuccess) result).getData();
                Log.d(TAG, "user exists: " + user.toString());
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        ((Result.Error) result).getMessage(),
                        Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "user does not exist: " + ((Result.Error) result).getMessage());
            }
        });

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
            updateItem(friendTextBoxesRecyclerAdapter, R.string.add_friends_username);
        });

        mainActivity.findViewById(R.id.button_save_activity).setOnClickListener(v -> { //TODO: finish to implement this method
            // Save the data
            String username = binding.friendsEmailFormEditText.getText().toString();
            Log.d(TAG, "username: " + username);
            userViewModel.isUserRegistered(username);

            //TODO: before the next part of the code we should put some ifs to check nulls values
            /*

            if(checkNullValues()){
                return;
            }
            Travels travel = new Travels();
            String id;
            String userId;
            try {
                userId = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (userId != null) {
                id = userId + System.currentTimeMillis();
            }else{
                id = System.currentTimeMillis() + "";
            }
            id = String.valueOf(id.hashCode());
            travel.setId(Long.parseLong(id));

            travel.setTitle(binding.titleFormEditText.getText().toString());
            travel.setDescription(binding.descriptionFormEditText.getText().toString());
            try {
                String endDateString = binding.editTxtToForm.getText().toString();
                String startDateString = binding.editTxtFromForm.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                Date endDate = sdf.parse(endDateString);

                Date startDate = sdf.parse(startDateString);

                travel.setStartDate(startDate);
                travel.setEndDate(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<TravelSegment> destinations = new ArrayList<TravelSegment>();
            destinations.add(new TravelSegment(binding.departureFormEditText.getText().toString()));
            destinations.add(new TravelSegment(binding.destinationFormEditText.getText().toString()));
            for(String s : destinationsText){
                TravelSegment segment = new TravelSegment(s);
                destinations.add(segment);
            }
            travel.setDestinations(destinations);

            List<TravelMember> members = new ArrayList<>();
            members.add(new TravelMember(userId, TravelMember.Role.CREATOR));
            String memberUsername = binding.friendsEmailFormEditText.getText().toString();
            if(!memberUsername.isEmpty()){
                if(checkIfUserExists(memberUsername)){
                    members.add(new TravelMember(memberUsername, TravelMember.Role.MEMBER));
                    Log.d(TAG, "checking firs user in list, it exists");
                }
            }
            for(String s : friendTextList){
                String memberId = s; //TODO: implement this method
                //check if the user exists
                TravelMember member = new TravelMember(memberId);
                members.add(member);
            }
            travel.setMembers(members);

            travelsViewModel.addTravel(travel);*/

            //TODO: implement the code to save the travel under users collection on firebase database
        });

    }

    public Travels buildTravel(){
        Travels travel = new Travels();

        String userId = getLoggedUserId();
        String travelId = buildTravelId(userId);
        String title = binding.titleFormEditText.getText().toString();
        String description = binding.descriptionFormEditText.getText().toString();

        String start = binding.editTxtFromForm.getText().toString();
        String end = binding.editTxtToForm.getText().toString();
        Date startDate = parseStringToDate(start);
        Date endDate = parseStringToDate(end);
        if(startDate == null || endDate == null){
            throw new RuntimeException("Error while parsing dates, impossible to build the travel");
        }

        String departure = binding.departureFormEditText.getText().toString();
        String destination = binding.destinationFormEditText.getText().toString();
        List<TravelSegment> destinations = buildDestinationsList(departure, destination);

        String firstMember = binding.friendsEmailFormEditText.getText().toString();
        List<TravelMember> members = buildFriendsList(firstMember);

        travel.setId(Long.parseLong(travelId));
        travel.setTitle(title);
        travel.setDescription(description);
        travel.setStartDate(startDate);
        travel.setEndDate(endDate);
        travel.setDestinations(destinations);
        travel.setMembers(members);

        return travel;
    }

    public List<TravelMember> buildFriendsList(String firstMember){
        List<TravelMember> members = new ArrayList<>();
        String userId = getLoggedUserId();
        TravelMember creator = new TravelMember(userId, TravelMember.Role.CREATOR);
        members.add(creator);
        if(!firstMember.isEmpty()){
            TravelMember member = new TravelMember(firstMember, TravelMember.Role.MEMBER);
            members.add(member);
        }
        for(String s : friendTextList){
            TravelMember member = new TravelMember(s, TravelMember.Role.MEMBER);
            members.add(member);
        }
        return members;
    }
    public List <TravelSegment> buildDestinationsList(String departure, String destination){
        List<TravelSegment> destinations = new ArrayList<>();
        destinations.add(new TravelSegment(departure));
        destinations.add(new TravelSegment(destination));
        for(String s : destinationsText){
            TravelSegment segment = new TravelSegment(s);
            destinations.add(segment);
        }
        return destinations;
    }
    public Date parseStringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public String buildTravelId(String userId){
        String hashId = (userId + System.currentTimeMillis()).hashCode() + "";
        return hashId;
    }
    public String getLoggedUserId(){
        String userId;
        try {
            userId = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
                    ID_TOKEN);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    private boolean checkNullValues() {
        boolean isNull = false;
        //mandatory fields are title, from, to, departure, destinations
        if (binding.titleFormEditText.getText().toString().isEmpty()) {
            binding.titleFormEditText.setError(getString(R.string.title_empty_error));
            isNull = true;
        }
        if (binding.editTxtFromForm.getText().toString().isEmpty()) {
            binding.editTxtFromForm.setError(getString(R.string.date_empty_error));
            isNull = true;
        }
        if (binding.editTxtToForm.getText().toString().isEmpty()) {
            binding.editTxtToForm.setError(getString(R.string.date_empty_error));
            isNull = true;
        }
        if (binding.departureFormEditText.getText().toString().isEmpty()) {
            binding.departureFormEditText.setError(getString(R.string.departure_empty_error));
            isNull = true;
        }
        if (binding.destinationFormEditText.getText().toString().isEmpty()) {
                binding.destinationFormEditText.setError(getString(R.string.destination_error));
                isNull = true;

        }
        return isNull;
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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        printdataset(destinationsText);
        printdataset(friendTextList);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(DESTINATIONS_TEXTS, (ArrayList<String>) destinationsText);
        outState.putStringArrayList(FRIENDS_TEXTS, (ArrayList<String>) friendTextList);
        outState.putStringArrayList(DESTINATIONS_HINTS, (ArrayList<String>) hintsList);
        outState.putStringArrayList(FRIENDS_HINTS, (ArrayList<String>) friendHintsList);
        outState.putString(DESTINATION, binding.destinationFormEditText.getText().toString());
        outState.putString(FRIEND, binding.friendsEmailFormEditText.getText().toString());
        outState.putString(TRAVEL_TITLE, binding.titleFormEditText.getText().toString());
        outState.putString(TRAVEL_DESCRIPTION, binding.descriptionFormEditText.getText().toString());
    }
}