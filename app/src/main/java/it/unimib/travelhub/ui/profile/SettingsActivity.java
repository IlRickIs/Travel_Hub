package it.unimib.travelhub.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.ActivityMainBinding;
import it.unimib.travelhub.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(v -> this.getOnBackPressedDispatcher().onBackPressed());

    }



}