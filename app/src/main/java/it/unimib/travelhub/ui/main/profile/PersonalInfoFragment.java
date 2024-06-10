package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.USERNAME;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.databinding.FragmentPersonalInfoBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.welcome.UserViewModel;
import it.unimib.travelhub.ui.welcome.UserViewModelFactory;
import it.unimib.travelhub.util.ServiceLocator;

public class PersonalInfoFragment extends Fragment {
    private static final String TAG = PersonalInfoFragment.class.getSimpleName();
    private FragmentPersonalInfoBinding binding;

    final Calendar myCalendar= Calendar.getInstance();
    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;

    public PersonalInfoFragment() {
        // Required empty public constructor
    }

    public static PersonalInfoFragment newInstance() {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatePickerDialog.OnDateSetListener date1 = (v, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
            Objects.requireNonNull(binding.textFieldBirth.getEditText()).setText(sdf.format(myCalendar.getTime()));
        };

        binding.textEditBirth.setOnClickListener(v -> {
            if (getContext() != null)
                new DatePickerDialog(getContext(),
                        date1,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            else
                Log.e(TAG, "Unexpected error: Context is null");
        });

        try {

            String username = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USERNAME);
            Objects.requireNonNull(binding.textFieldUsername.getEditText()).setHint(username);
            binding.textFieldUsername.getEditText().setText(username);

            String name = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_name");
            Objects.requireNonNull(binding.textFieldName.getEditText()).setHint(name);
            binding.textFieldName.getEditText().setText(name);

            String surname = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_surname");
            Objects.requireNonNull(binding.textFieldSurname.getEditText()).setHint(surname);
            binding.textFieldSurname.getEditText().setText(surname);

            String birthDate = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_birthDate");
            Objects.requireNonNull(binding.textFieldBirth.getEditText()).setHint(birthDate);
            binding.textFieldBirth.getEditText().setText(birthDate);

        } catch (Exception e) {
            Log.e(TAG, "Error while reading data from encrypted shared preferences", e);
        }


        binding.buttonLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");

            AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
            builder1.setMessage(R.string.logout_message);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> userViewModel.logout(dataEncryptionUtil, requireActivity().getApplication()).observe(getViewLifecycleOwner(), result -> {
                        if (result.isSuccess()) {
                            try {
                                String mail = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
                                String password = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
                                String username = dataEncryptionUtil.
                                        readSecretDataWithEncryptedSharedPreferences(
                                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
                                Log.d(TAG, "Username from encrypted SharedPref: " + username);
                                Log.d(TAG, "Email address from encrypted SharedPref: " + mail);
                                Log.d(TAG, "Password from encrypted SharedPref: " + password);

                            } catch (GeneralSecurityException | IOException e) {
                                e.printStackTrace();
                            }
                            Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_welcomeActivity);
                            requireActivity().finish();
                        } else {
                            Snackbar.make(view,
                                    requireActivity().getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }));

            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();


        });

        binding.buttonSaveSettings.setOnClickListener(v -> {
            if (somethingIsChanged()) {
                if (usernameIsChanged()) {
                    checkIfUsernameIsAlreadyTaken();
                } else {
                    saveUserDataRemote();
                }
            } else {
                Log.d(TAG, "Nothing is changed");
                Snackbar.make(requireView(),
                        requireActivity().getString(R.string.personal_info_saved),
                        Snackbar.LENGTH_SHORT).show();
            }

        });

        binding.buttonBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private Boolean somethingIsChanged() {
        String username = Objects.requireNonNull(binding.textFieldUsername.getEditText()).getText().toString();
        String name = Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString();
        String surname = Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString();
        String birthDate = Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString();

        try {
            String saved_username = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
            String saved_name = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_name");
            String saved_surname = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_surname");
            String saved_birthDate = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_birthDate");

            boolean nameCheck = name == null ? saved_name != null : !name.equals(saved_name == null ? "" : saved_name);
            boolean surnameCheck = surname == null ? saved_surname != null : !surname.equals(saved_surname == null ? "" : saved_surname);
            boolean birthDateCheck = birthDate == null ? saved_birthDate != null : !birthDate.equals(saved_birthDate == null ? "" : saved_birthDate);

            return !username.equals(saved_username) ||
                    nameCheck || surnameCheck || birthDateCheck;
        } catch (Exception e) {
            Log.d(TAG, "Error while reading data from encrypted shared preferences - somethingIsChanged - ", e);
            return false;
        }
    }

    private void checkIfUsernameIsAlreadyTaken() {
        String username = Objects.requireNonNull(binding.textFieldUsername.getEditText()).getText().toString();

        if (username.isEmpty()) {
            binding.textFieldUsername.setError(getString(R.string.username_field_empty));
        } else {
            binding.textFieldUsername.setError(null);
            binding.textFieldUsername.setErrorEnabled(false);
            try {
                String saved_username = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");

                if (!username.equals(saved_username)) {
                    userViewModel.isUsernameAlreadyTaken(username);

                    Observer<Result> observer = new Observer<Result>() {
                        @Override
                        public void onChanged(Result result) {
                            if (result.isSuccess()) {
                                if (((Result.UserResponseSuccess) result).getData() != null) {
                                    binding.textFieldUsername.setError(getString(R.string.username_already_taken));
                                }else {
                                    Snackbar.make(requireView(),
                                            requireActivity().getString(R.string.unexpected_error),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.textFieldUsername.setError(null);
                                binding.textFieldUsername.setErrorEnabled(false);
                                if (somethingIsChanged())
                                    saveUserDataRemote();
                            }

                            userViewModel.getIsUsernameAlreadyTaken().removeObserver(this);
                        }
                    };

                    userViewModel.getIsUsernameAlreadyTaken().observe(getViewLifecycleOwner(), observer);
                } else {
                    saveUserDataRemote();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error while reading data from encrypted shared preferences - checkIfUsernameIsAlreadyTaken - ", e);
            }

        }
    }

    private void saveUserDataRemote() {
        String username = Objects.requireNonNull(binding.textFieldUsername.getEditText()).getText().toString();
        String name = Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString();
        String surname = Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString();
        Long birthDate = Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty() ?
                null : parseStringToDateLong(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString());

        try {
            String idToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN);
            String oldUsername = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
            String email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            User user = new User(username, name, surname, birthDate, null, email, idToken);

            userViewModel.updateUserData(oldUsername, user);

            Observer<Result> observer = new Observer<Result>() {
                @Override
                public void onChanged(Result result) {
                    if (result.isSuccess()) {
                        try {
                            Log.d(TAG, "User data saved successfully");

                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username", username);
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_name", name);
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_surname", surname);
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_birthDate",
                                    parseDateToString(birthDate == null ? null : new Date(birthDate)));

                            Snackbar.make(requireView(),
                                    requireActivity().getString(R.string.personal_info_saved),
                                    Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(TAG, "Error while writing data to encrypted shared preferences", e);
                        }
                    } else {
                        Snackbar.make(requireView(),
                                requireActivity().getString(R.string.unexpected_error),
                                Snackbar.LENGTH_SHORT).show();
                    }

                    userViewModel.getUserMutableLiveData().removeObserver(this);
                }
            };

            userViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), observer);
        } catch (Exception e) {
            Log.d(TAG, "Error while reading data from encrypted shared preferences - saveUserDataRemote - ", e);
        }


    }

    private String parseDateToString(Date birthDate) {
        if (birthDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        return sdf.format(birthDate);
    }

    public Long parseStringToDateLong(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate == null ? 0L : parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private boolean usernameIsChanged() {
        String username = Objects.requireNonNull(binding.textFieldUsername.getEditText()).getText().toString();
        try {
            String saved_username = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
            return !username.equals(saved_username);
        } catch (Exception e) {
            Log.d(TAG, "Error while reading data from encrypted shared preferences - usernameIsChanged - ", e);
            return false;
        }
    }

}