package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.PASSWORD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

        userViewModel.getIsUsernameAlreadyTaken().observe(getViewLifecycleOwner(), result -> {
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
                saveUserDataRemote();
            }
        });

        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            String username = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
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

            String userCity = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_city");
            Objects.requireNonNull(binding.textFieldUserCity.getEditText()).setHint(userCity);
            binding.textFieldUserCity.getEditText().setText(userCity);


            String email = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);

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
            checkIfUsernameIsAlreadyTaken();
        });

        binding.buttonBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
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
                }
            } catch (Exception e) {
                Log.d(TAG, "Error while reading data from encrypted shared preferences", e);
            }

        }
    }

    private void saveUserDataRemote() {
        String username = Objects.requireNonNull(binding.textFieldUsername.getEditText()).getText().toString();
        String name = Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldName.getEditText()).getText().toString();
        String surname = Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString().isEmpty() ?
                null : Objects.requireNonNull(binding.textFieldSurname.getEditText()).getText().toString();
        Date birthDate = Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString().isEmpty() ?
                null : parseStringToDate(Objects.requireNonNull(binding.textFieldBirth.getEditText()).getText().toString());
        //String userCity = Objects.requireNonNull(binding.textFieldUserCity.getEditText()).getText().toString();

        //TODO: Da implementare

    }

    public Date parseStringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY); //TODO: Da cambiare in base al formato della data
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }
}