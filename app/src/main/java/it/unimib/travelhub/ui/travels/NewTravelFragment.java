package it.unimib.travelhub.ui.travels;

import static it.unimib.travelhub.util.Constants.DESTINATIONS_HINTS;
import static it.unimib.travelhub.util.Constants.DESTINATIONS_TEXTS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.FRIEND;
import static it.unimib.travelhub.util.Constants.FRIENDS_HINTS;
import static it.unimib.travelhub.util.Constants.FRIENDS_TEXTS;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.USERNAME;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

public class NewTravelFragment extends Fragment {
    private FragmentEditTravelBinding binding;

    private TextBoxesRecyclerAdapter textBoxesRecyclerAdapter;

    private TextBoxesRecyclerAdapter friendTextBoxesRecyclerAdapter;
    private static final String TAG = NewTravelFragment.class.getSimpleName();
    final Calendar myCalendar= Calendar.getInstance();
    private List<String> friendTextList;
    private List<String> destinationsText;
    private List<String> hintsList;
    private List<String> friendHintsList;
    private Activity mainActivity;
    private TravelsViewModel travelsViewModel;
    private DataEncryptionUtil dataEncryptionUtil;

    private List<TravelMember> memberList;
    private UserViewModel userViewModel;
    AtomicBoolean exists = new AtomicBoolean(false);
    public NewTravelFragment() {
    }

    public static NewTravelFragment newInstance(String param1, String param2) {
        NewTravelFragment fragment = new NewTravelFragment();
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
            //binding.titleFormEditText.setText(savedInstanceState.getString(TRAVEL_TITLE));
            //binding.descriptionFormEditText.setText(savedInstanceState.getString(TRAVEL_DESCRIPTION));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTravelBinding.inflate(inflater, container, false);
        mainActivity = (Activity) requireActivity();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
                builder1.setMessage("Are you sure you want to come back? Your new travel will be lost.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            dialog.cancel();
                            requireActivity().finish();
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

    private void updateLabel(EditText editText) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, getResources().getConfiguration().getLocales().get(0));
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        memberList = new ArrayList<>();
        DatePickerDialog.OnDateSetListener date1 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(binding.editTxtFromForm);
        };
        DatePickerDialog.OnDateSetListener date2 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(binding.editTxtToForm);
        };
        binding.editTxtFromForm.setOnClickListener(v ->
        {
            if (getContext() != null)
                new DatePickerDialog(
                        getContext(), date1,
                        Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                myCalendar.get(Calendar.YEAR) :
                                Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[2]),
                        Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                myCalendar.get(Calendar.MONTH) :
                                Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[1]) - 1,
                        Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                myCalendar.get(Calendar.DAY_OF_MONTH) :
                                Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[0])).show();
        });

        binding.editTxtToForm.setOnClickListener(v ->
        {
            if (getContext() != null)
                new DatePickerDialog(
                        getContext(), date2,
                        Objects.requireNonNull(binding.editTxtToForm.getText()).toString().isEmpty() ?
                                (Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                        myCalendar.get(Calendar.YEAR) :
                                        Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[2])) :
                                Integer.parseInt(binding.editTxtToForm.getText().toString().split("/")[2]),
                        Objects.requireNonNull(binding.editTxtToForm.getText()).toString().isEmpty() ?
                                (Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                        myCalendar.get(Calendar.MONTH) :
                                        Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[1]) - 1) :
                                Integer.parseInt(binding.editTxtToForm.getText().toString().split("/")[1]) - 1,
                        Objects.requireNonNull(binding.editTxtToForm.getText()).toString().isEmpty() ?
                                (Objects.requireNonNull(binding.editTxtFromForm.getText()).toString().isEmpty() ?
                                        myCalendar.get(Calendar.DAY_OF_MONTH) :
                                        Integer.parseInt(binding.editTxtFromForm.getText().toString().split("/")[0])) :
                                Integer.parseInt(binding.editTxtToForm.getText().toString().split("/")[0])).show();
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

       /* binding.recyclerDestinations.setLayoutManager(mLayoutManager);
        binding.recyclerDestinations.setAdapter(textBoxesRecyclerAdapter);

        binding.addDestinationButton.setOnClickListener(v -> {
            updateItem(textBoxesRecyclerAdapter, R.string.destination);
        });*/
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

        //make the save button non clickable and make it appear it is not clickable
        mainActivity.findViewById(R.id.button_save_activity).setClickable(false);
        mainActivity.findViewById(R.id.button_save_activity).setAlpha(0.5f);

        binding.addDestinationButton.setOnClickListener(v -> {
            if(checkNullValues()){
                return;
            }
            checkUsers();
            observeUserRegistration();
        });

    }

    private void observeUserRegistration() {
        /*userViewModel.getIsUserRegistered().observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()){
                List<User> users = ((Result.UsersResponseSuccess) result).getData();
                Log.d(TAG, "user exists: " + users.toString());
                for(User u : users){
                    TravelMember member = new TravelMember(u.getUsername(),
                            u.getIdToken(),
                            TravelMember.Role.MEMBER);
                    memberList.add(member);
                }
                Travels travel = buildTravel();
                //travelsViewModel.addTravel(travels);
                //attachTravelObserver();
                userViewModel.getIsUserRegistered().removeObservers(getViewLifecycleOwner());
                goToNewFragment(travel);
                //detach observer
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        ((Result.Error) result).getMessage(),
                        Snackbar.LENGTH_SHORT).show();
            }
        });*/

        Observer<Result> myObserver = new Observer<Result>() {
            public void onChanged(Result result) {
                if(result.isSuccess()){
                    List<User> users = ((Result.UsersResponseSuccess) result).getData();
                    Log.d(TAG, "user exists: " + users.toString());
                    for(User u : users){
                        TravelMember member = new TravelMember(u.getUsername(),
                                u.getIdToken(),
                                TravelMember.Role.MEMBER);
                        memberList.add(member);
                    }
                    Travels travel = buildTravel();
                    //travelsViewModel.addTravel(travels);
                    //attachTravelObserver();
                    userViewModel.getIsUserRegistered().removeObservers(getViewLifecycleOwner());
                    goToNewFragment(travel);
                    //detach observer
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            ((Result.Error) result).getMessage(),
                            Snackbar.LENGTH_SHORT).show();
                }
                userViewModel.getIsUserRegistered().removeObserver(this);
            }
        };
        userViewModel.getIsUserRegistered().observe(getViewLifecycleOwner(), myObserver);
    }

    private void goToNewFragment(Travels travel){
        Bundle bundle = new Bundle();
        bundle.putSerializable("travel", travel);

        NewTravelSegment newTravelSegment = new NewTravelSegment();
        newTravelSegment.setArguments(bundle);

        // navigate to EditTravelSegment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityAddFragmentContainerView, newTravelSegment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void checkUsers(){
        List<String> userToCheck = new ArrayList<>();
        String firstUser = binding.friendsEmailFormEditText.getText().toString();
        if(!firstUser.isEmpty() && firstUser != null){
            if( !firstUser.equals(getLoggedUsername()))
                userToCheck.add(firstUser);
        }
        for(String s : friendTextList){
            if(s != null && !s.isEmpty()) {
                if( !s.equals(getLoggedUsername()))
                    userToCheck.add(s);
            }
        }
        if(userToCheck.isEmpty()){
//            Travels upload = buildTravel();
//            travelsViewModel.addTravel(upload);
//            attachTravelObserver();
            //here we dont need to do anything just build the travel
            goToNewFragment(buildTravel());
        }else {
            Log.d(TAG, "checkUsers: " + userToCheck);
            userViewModel.checkUsernames(userToCheck);
        }
    }

    public Travels buildTravel(){
        Travels travel = new Travels();

        String userId = getLoggedUsername();
        String travelId = buildTravelId(userId);
        String title = binding.titleFormEditText.getText().toString();
        String description = binding.descriptionFormEditText.getText().toString();

        String start = binding.editTxtFromForm.getText().toString() + " 00:00:00";
        String end = binding.editTxtToForm.getText().toString() + " 23:59:59";
        Date startDate = parseStringToDate(start);
        Date endDate = parseStringToDate(end);
        if(startDate == null || endDate == null){
            throw new RuntimeException("Error while parsing dates, impossible to build the travel");
        }

        String departure = binding.departureFormEditText.getText().toString();
        List<TravelSegment> destinations = buildDestinationsList(departure);

        String firstMember = binding.friendsEmailFormEditText.getText().toString();
        List<TravelMember> members = buildFriendsList();

        travel.setId(Long.parseLong(travelId));
        travel.setTitle(title);
        travel.setDescription(description);
        travel.setStartDate(startDate);
        travel.setEndDate(endDate);
        travel.setDestinations(destinations);
        travel.setMembers(members);

        return travel;
    }

    public List<TravelMember> buildFriendsList(){
        List<TravelMember> members = new ArrayList<>();
        TravelMember creator = new TravelMember(TravelMember.Role.CREATOR);
        creator.setUsername(getLoggedUsername());
        creator.setIdToken(getLoggedIdToken());
        members.add(creator);
        members.addAll(memberList);
        return members;
    }
    public List <TravelSegment> buildDestinationsList(String departure){
        List<TravelSegment> destinations = new ArrayList<>();
        TravelSegment start = new TravelSegment(departure);
        start.setDateTo(parseStringToDate(binding.editTxtFromForm.getText().toString() + " 00:00:00"));
        destinations.add(start);
        return destinations;
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

    public String buildTravelId(String userId){
        String hashId = (userId + System.currentTimeMillis()).hashCode() + "";
        return hashId;
    }
    public String getLoggedUsername(){
        String userId;
        try {
            userId = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
                    USERNAME);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    public String getLoggedIdToken(){
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
        }else{
            binding.titleFormEditText.setError(null);
        }
        if (binding.editTxtFromForm.getText().toString().isEmpty()) {
            binding.editTxtFromForm.setError(getString(R.string.date_empty_error));
            isNull = true;
        }else{
            binding.editTxtFromForm.setError(null);
        }
        if (binding.editTxtToForm.getText().toString().isEmpty()) {
            binding.editTxtToForm.setError(getString(R.string.date_empty_error));
            isNull = true;
        }else{
            binding.editTxtToForm.setError(null);
        }
        if (binding.departureFormEditText.getText().toString().isEmpty()) {
            binding.departureFormEditText.setError(getString(R.string.departure_empty_error));
            isNull = true;
        }else{
            binding.departureFormEditText.setError(null);
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
        userViewModel.getIsUserRegistered().removeObservers(getViewLifecycleOwner());
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(DESTINATIONS_TEXTS, (ArrayList<String>) destinationsText);
        outState.putStringArrayList(FRIENDS_TEXTS, (ArrayList<String>) friendTextList);
        outState.putStringArrayList(DESTINATIONS_HINTS, (ArrayList<String>) hintsList);
        outState.putStringArrayList(FRIENDS_HINTS, (ArrayList<String>) friendHintsList);
        outState.putString(FRIEND, binding.friendsEmailFormEditText.getText().toString());
        //outState.putString(TRAVEL_TITLE, binding.titleFormEditText.getText().toString());
        //outState.putString(TRAVEL_DESCRIPTION, binding.descriptionFormEditText.getText().toString());
    }
}