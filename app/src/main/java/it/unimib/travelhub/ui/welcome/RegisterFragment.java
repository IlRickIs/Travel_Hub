package it.unimib.travelhub.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.travelhub.GlobalClass;
import it.unimib.travelhub.databinding.FragmentRegisterBinding;
import it.unimib.travelhub.model.IValidator;
import it.unimib.travelhub.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private FragmentRegisterBinding binding;

    private IValidator validator;

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

        validator = ServiceLocator.getInstance().getCredentialsValidator(GlobalClass.getContext());
        binding.buttonRegister.setOnClickListener(V -> {

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}