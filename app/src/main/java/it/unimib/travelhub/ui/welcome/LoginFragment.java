package it.unimib.travelhub.ui.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.FragmentLoginBinding;
import it.unimib.travelhub.util.IValidator;
import it.unimib.travelhub.util.ServiceLocator;

public class LoginFragment extends Fragment {

    IValidator myValidator = ServiceLocator.getInstance().getCredentialsValidator();

    private static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding binding;
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
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.buttonLogin.setOnClickListener(V -> {
            String email = binding.txtInputEditUser.getText().toString();
            String password = binding.txtInputEditPwd.getText().toString();
            if (isEmailOk(email) & isPasswordOk(password)) {
                Log.d(TAG, "Email and password are ok");
                /*
                saveLoginData(email, password);

                startActivityBasedOnCondition(NewsPreferencesActivity.class,
                        R.id.navigate_to_newsPreferencesActivity);
                 */
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private boolean isEmailOk(String email) {
        if (!myValidator.validateMail(email)) {
            binding.txtInputEditUser.setError(getString(R.string.error_email));
            return false;
        } else {
            binding.txtInputEditUser.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        return true;
    }

}