package it.unimib.travelhub.ui.welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private boolean isEmailOk(String email) {
        // Check if the email is valid through the use of this library:
        // https://commons.apache.org/proper/commons-validator/
        if (!EmailValidator.getInstance().isValid((email))) {
            binding.txtInputEditUser.setError(getString(R.string.error_email));
            return false;
        } else {
            binding.txtInputEditUser.setError(null);
            return true;
        }
    }

}