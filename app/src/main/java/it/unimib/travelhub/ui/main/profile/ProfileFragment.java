package it.unimib.travelhub.ui.main.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import it.unimib.travelhub.R;
import it.unimib.travelhub.SecurityFragment;
import it.unimib.travelhub.SettingsFragment;
import it.unimib.travelhub.SubscriptionFragment;
import it.unimib.travelhub.TermsOfPrivacyFragment;
import it.unimib.travelhub.databinding.FragmentLoginBinding;
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

    private FragmentProfileBinding binding;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.profileFragmentContainer, PersonalInfoFragment.class, null)
                .commit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonPersonalInformations.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.profileFragmentContainer, PersonalInfoFragment.class, null)
                    .commit();
        });

        binding.buttonSettings.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.profileFragmentContainer, SettingsFragment.class, null)
                    .commit();
        });

        binding.buttonSubscription.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.profileFragmentContainer, SubscriptionFragment.class, null)
                    .commit();
        });

        binding.buttonSecurity.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.profileFragmentContainer, SecurityFragment.class, null)
                    .commit();
        });
        binding.buttonTermsOfPrivacy.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.profileFragmentContainer, TermsOfPrivacyFragment.class, null)
                    .commit();
        });
    }
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}