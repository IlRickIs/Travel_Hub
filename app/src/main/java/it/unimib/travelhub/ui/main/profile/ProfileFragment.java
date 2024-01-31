package it.unimib.travelhub.ui.main.profile;

import static it.unimib.travelhub.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import it.unimib.travelhub.R;
import it.unimib.travelhub.crypto_util.DataEncryptionUtil;
import it.unimib.travelhub.databinding.FragmentProfileBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String name, surname, username, email;

    private FragmentProfileBinding binding;

    private DataEncryptionUtil dataEncryptionUtil;

    // TODO: Rename and change types of parameters

    private String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        //edit text in header_profile_drawer layout
        View headerView = binding.profileDrawerNavigation.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.textViewUsername)).setText(username);
        ((TextView) headerView.findViewById(R.id.textViewEmail)).setText(email);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.open();
            }
        });

        NavigationView navigationView = binding.profileDrawerNavigation;
        navigationView.setNavigationItemSelectedListener(item -> {
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



    }
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}