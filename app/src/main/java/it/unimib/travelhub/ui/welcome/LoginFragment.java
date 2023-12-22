package it.unimib.travelhub.ui.welcome;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.FragmentLoginBinding;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.util.IValidator;
import it.unimib.travelhub.util.ServiceLocator;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.util.ValidationResult;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.PASSWORD;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import it.unimib.travelhub.GlobalClass;
public class LoginFragment extends Fragment {

    IValidator myValidator = ServiceLocator.getInstance().getCredentialsValidator(GlobalClass.getContext());

    private static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding binding;

    private DataEncryptionUtil dataEncryptionUtil;

    private static final boolean USE_NAVIGATION_COMPONENT = true;

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
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        try {
            String mail = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            String password = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
            Log.d(TAG, "Email address from encrypted SharedPref: " + mail);
            Log.d(TAG, "Password from encrypted SharedPref: " + password);

            if(mail != null && password != null){
                Log.d(TAG, "starting main activity");
                startActivityBasedOnCondition(MainActivity.class,
                        R.id.action_loginFragment_to_mainActivity);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


        binding.buttonLogin.setOnClickListener(V -> {
            String email = binding.txtInputEditUser.getText().toString();
            String password = binding.txtInputEditPwd.getText().toString();
            if (isEmailOk(email) & isPasswordOk(password)) {
                Log.d(TAG, "Email and password are ok");
                saveLoginData(email, password);

                startActivityBasedOnCondition(MainActivity.class,
                        R.id.action_loginFragment_to_mainActivity);

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

    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

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


}