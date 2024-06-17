package it.unimib.travelhub.ui.welcome;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.travelhub.util.Constants.INVALID_USER_ERROR;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.USERNAME;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.TravelMember;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsernameChoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsernameChoiceFragment extends Fragment {

    private static final String TAG = UsernameChoiceFragment.class.getSimpleName();
    private User user;
    private Button saveUsernameButton;
    private EditText usernameEditText;
    private UserViewModel userViewModel;

    private DataEncryptionUtil dataEncryptionUtil;
    private SharedPreferencesUtil dataUtil;
    public UsernameChoiceFragment() {
        // Required empty public constructor
    }

    public static UsernameChoiceFragment newInstance() {
        UsernameChoiceFragment fragment = new UsernameChoiceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }else{
            Log.d(TAG, "User is null");
        }

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        dataUtil = new SharedPreferencesUtil(requireActivity().getApplication());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username_choice, container, false);

       saveUsernameButton = (Button) view.findViewById(R.id.button_save_username);
       usernameEditText = (EditText) view.findViewById(R.id.username_edit_text);
        saveUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().isEmpty()){
                    usernameEditText.setError(getString(R.string.username_error));
                    return;
                }else{
                    user.setUsername(usernameEditText.getText().toString());
                    Log.d(TAG, "Username: " + user.getUsername() + " idToken: " + user.getIdToken());
                    userViewModel.isUsernameAlreadyTaken(user.getUsername());
                    observeUsernameAlreadyTaken();

                }
            }

        });

        return view;
    }

    private void googleSingIn(){
        String idToken = user.getIdToken();
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate with Firebase.
            userViewModel.getGoogleUserMutableLiveData(user).observe(getViewLifecycleOwner(), authenticationResult -> {
                if (authenticationResult.isSuccess()) {
                    User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                    saveLoginData(user.getEmail(), user.getUsername(), null, user.getIdToken(), user.getName(), user.getSurname(), user.getBirthDate());
                    userViewModel.setAuthenticationError(false);
                    retrieveUserInformationAndStartActivity(user, R.id.action_loginFragment_to_mainActivity);
                } else {
                    userViewModel.setAuthenticationError(true);
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void observeUsernameAlreadyTaken(){
        Observer<Result> observer = new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Error) {
                    Log.d(TAG, "check result: " + ((Result.Error) result).getMessage());
                    googleSingIn();
                }else{
                    Log.d(TAG, "check result: " + ((Result.UserResponseSuccess) result).getData().toString());
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            getString(R.string.error_username_not_available),
                            Snackbar.LENGTH_SHORT).show();
                }
                userViewModel.getIsUsernameAlreadyTaken().removeObserver(this);
            }
        };

        userViewModel.getIsUsernameAlreadyTaken().observe(getViewLifecycleOwner(), observer);


    }

    private void saveLoginData(String email, String username, String password, String idToken, String name, String surname, Long birthdate) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USERNAME, username);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_name", name);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_surname", surname);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration().getLocales().get(0));
            if (birthdate != null)
                dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_birthDate", sdf.format(new Date(birthdate)));
            else dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_birthDate", null);


        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_login_user_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    private void retrieveUserInformationAndStartActivity(User user, int destination) {
        userViewModel.getUserMutableLiveData(user.getEmail(), user.getIdToken(), false).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        User userWithPreferences = ((Result.UserResponseSuccess) result).getData();
                        startActivityBasedOnCondition(MainActivity.class, destination);
                    } else {
                        userViewModel.setAuthenticationError(true);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
            requireActivity().finish();
    }
}