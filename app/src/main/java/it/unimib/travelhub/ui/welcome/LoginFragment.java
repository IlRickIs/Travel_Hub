package it.unimib.travelhub.ui.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.databinding.FragmentLoginBinding;
import it.unimib.travelhub.model.Result;
import it.unimib.travelhub.model.User;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.model.IValidator;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.model.ValidationResult;
import it.unimib.travelhub.model.Result;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.travelhub.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.travelhub.util.Constants.ID_TOKEN;
import static it.unimib.travelhub.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.travelhub.util.Constants.INVALID_USER_ERROR;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import it.unimib.travelhub.GlobalClass;
public class LoginFragment extends Fragment {

    IValidator myValidator;

    private static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding binding;

    private DataEncryptionUtil dataEncryptionUtil;

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;

    private UserViewModel userViewModel;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private LinearProgressIndicator progressIndicator;

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myValidator = ServiceLocator.getInstance().getCredentialsValidator(GlobalClass.getContext());

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                                saveLoginData(user.getEmail(), null, user.getIdToken());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, R.id.action_loginFragment_to_mainActivity);
                            } else {
                                userViewModel.setAuthenticationError(true);
                               // progressIndicator.setVisibility(View.GONE);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        testDatabaseUsername("paolo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inflate the layout for this fragment
        /*
        if(userViewModel.getLoggedUser() != null) {
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

                if (mail != null && password != null && username != null) {
                    Log.d(TAG, "starting main activity");
                    startActivityBasedOnCondition(MainActivity.class,
                            R.id.action_loginFragment_to_mainActivity);
                }
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        */

        binding.buttonLogin.setOnClickListener(V -> {
            String email = binding.txtInputEditUser.getText().toString();
            String password = binding.txtInputEditPwd.getText().toString();

            if (isEmailOk(email) & isPasswordOk(password)) {
                Log.d(TAG, "Email and password are ok");
                if(!userViewModel.isAuthenticationError()){
                    userViewModel.getUserMutableLiveData(email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);
                                    startActivityBasedOnCondition(MainActivity.class,
                                            R.id.action_loginFragment_to_mainActivity);

                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, true);
                }
            } else {
                Log.d(TAG, "Email and password are NOT ok");
            }
        });

        binding.buttonRegister.setOnClickListener(V ->
        {
            NavController navController = Navigation.findNavController(view);
            NavDirections val = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
            navController.navigate(val);
        });

        binding.buttonGoogleLogin.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_no_google_account_found_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private boolean isEmailOk(String email) {
        ValidationResult validation = myValidator.validateMail(email);
        if (!validation.isSuccess()) {
            binding.txtInputEditUser.setError(validation.getMessage().toString());
            return false;
        } else {
            binding.txtInputEditUser.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        ValidationResult validation = myValidator.validatePassword(password);
        if(!validation.isSuccess()){
            //binding.txtInputLayoutPwd.setError(validation.getMessage().toString());
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    validation.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
            return false;
        }else{
            //binding.txtInputLayoutPwd.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password, String idToken) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
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
        //progressIndicator.setVisibility(View.VISIBLE);
        userViewModel.getUserMutableLiveData(user.getEmail(), user.getIdToken(), false).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        User userWithPreferences = ((Result.UserResponseSuccess) result).getData();
                       // progressIndicator.setVisibility(View.GONE);
                        startActivityBasedOnCondition(MainActivity.class, destination);
                    } else {
                        userViewModel.setAuthenticationError(true);
                       // progressIndicator.setVisibility(View.GONE);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void testDatabaseUsername(String username) {
        Log.d(TAG, "metodo da cancellare");

    }


}