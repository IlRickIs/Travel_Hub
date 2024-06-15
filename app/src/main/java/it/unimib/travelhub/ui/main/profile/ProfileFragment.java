package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.travelhub.util.Constants.PICS_FOLDER;
import static it.unimib.travelhub.util.Constants.PROFILE_PICTURE_FILE_NAME;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.databinding.FragmentProfileBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private String name, surname, username, email;
    private FragmentProfileBinding binding;
    private DataEncryptionUtil dataEncryptionUtil;
    private final String TAG = "ProfileFragment";
    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        try {
            username = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "username");
            name = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_name");
            surname = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "user_surname");
            email = dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, "email_address");
        } catch (Exception e) {
            Log.e(TAG, "Error while reading data from encrypted shared preferences", e);
        }
        binding.textViewUsername.setText(username);
        View headerView = binding.profileDrawerNavigation.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.textViewUsername)).setText(username);
        ((TextView) headerView.findViewById(R.id.textViewEmail)).setText(email);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) {
            Log.e(TAG, "Context is null");
            return;
        }
        String profileImagePath = getContext().getFilesDir() + PICS_FOLDER + PROFILE_PICTURE_FILE_NAME;
        try {
            File file = new File(profileImagePath);
            if (file.exists()) {
                Uri imageUri = Uri.fromFile(file);
                binding.imageViewUsername.setImageURI(imageUri);
            } else {
                Log.d(TAG, "File does not exist");
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while reading profile image", e);
        }

        binding.buttonMenu.setOnClickListener(v -> binding.drawerLayout.open());

        NavigationView navigationViewProfile = binding.profileDrawerNavigation;
        navigationViewProfile.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.personalInfoFragment){
                NavController navController = Navigation.findNavController(view);
                NavDirections val = ProfileFragmentDirections.actionProfileFragmentToPersonalInfoFragment();
                navController.navigate(val);
                binding.drawerLayout.close();
            }
            else if(item.getItemId() == R.id.settingsFragment){
                NavController navController = Navigation.findNavController(view);
                NavDirections val = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment();
                navController.navigate(val);
                binding.drawerLayout.close();
            }
            else if(item.getItemId() == R.id.subscriptionFragment){
                NavController navController = Navigation.findNavController(view);
                NavDirections val = ProfileFragmentDirections.actionProfileFragmentToSubscriptionFragment();
                navController.navigate(val);
                binding.drawerLayout.close();
            }
            else if(item.getItemId() == R.id.privacyAndSecurityFragment){
                NavController navController = Navigation.findNavController(view);
                NavDirections val = ProfileFragmentDirections.actionProfileFragmentToPrivacyAndSecurityFragment();
                navController.navigate(val);
                binding.drawerLayout.close();
            }
            return true;
        });


        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_profile_fragment, OngoingTravelsFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        binding.ongoingActivityFragment.setOnClickListener(v -> fragmentManager.beginTransaction()
                .replace(R.id.nav_profile_fragment, OngoingTravelsFragment.class, null)
                .setReorderingAllowed(true)
                .commit());

        binding.terminatedActivityFragment.setOnClickListener(v -> fragmentManager.beginTransaction()
                .replace(R.id.nav_profile_fragment, TerminatedTravelsFragment.class, null)
                .setReorderingAllowed(true)
                .commit());
    }
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}