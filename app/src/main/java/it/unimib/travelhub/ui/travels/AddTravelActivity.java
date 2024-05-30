package it.unimib.travelhub.ui.travels;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import it.unimib.travelhub.R;
import it.unimib.travelhub.data.repository.travels.ITravelsRepository;
import it.unimib.travelhub.databinding.ActivityAddTravelBinding;
import it.unimib.travelhub.model.Travels;
import it.unimib.travelhub.ui.main.MainActivity;
import it.unimib.travelhub.util.ServiceLocator;

public class AddTravelActivity extends AppCompatActivity {

private ActivityAddTravelBinding binding;
private FragmentActivityAddViewModel viewModel;
private Travels myTravel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityAddTravelBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

     viewModel = new ViewModelProvider(this).get(FragmentActivityAddViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.navigation_edit, R.id.navigation_images, R.id.navigation_map_preview)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.activityAddFragmentContainerView);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.buttonBack.setOnClickListener(v -> {
            this.getOnBackPressedDispatcher().onBackPressed();
        });

        binding.buttonSaveActivity.setOnClickListener(v -> {
            myTravel = viewModel.getTravel().getValue();
            if(myTravel == null){
                Snackbar.make(v, "Please fill all the fields", Snackbar.LENGTH_LONG).show();
            }else{
                ServiceLocator.getInstance().getTravelsRepository(
                       getApplication()
                ).addTravel(myTravel);
            }
        });
    }
}