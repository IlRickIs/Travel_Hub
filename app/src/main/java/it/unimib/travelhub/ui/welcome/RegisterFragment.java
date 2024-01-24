package it.unimib.travelhub.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.travelhub.GlobalClass;
import it.unimib.travelhub.databinding.FragmentRegisterBinding;
import it.unimib.travelhub.model.IValidator;
import it.unimib.travelhub.model.ValidationResult;
import it.unimib.travelhub.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private FragmentRegisterBinding binding;
    private UserViewModel userViewModel;

    private IValidator myValidator;


    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
        myValidator = ServiceLocator.getInstance().getCredentialsValidator(GlobalClass.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String email = binding.txtInputEditUser.getText().toString();
        String password = binding.txtInputEditPwd.getText().toString();
        String username = binding.txtInputEditName.getText().toString();
        binding.buttonRegister.setOnClickListener(V -> {
            if(isEmailOk(email) && isPasswordOk(password)){

            }
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
}