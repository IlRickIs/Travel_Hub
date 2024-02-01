package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.EMAIL_ADDRESS;
import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

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

import java.util.Objects;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.data.repository.user.IUserRepository;
import it.unimib.travelhub.databinding.FragmentPersonalInfoBinding;
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
                    (dialog, id) -> userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                        if (result.isSuccess()) {
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

        binding.buttonBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }
}