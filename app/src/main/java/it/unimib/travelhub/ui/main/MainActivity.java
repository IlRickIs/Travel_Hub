package it.unimib.travelhub.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import it.unimib.travelhub.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_menu = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.communityFragment,
                R.id.addFragment,
                R.id.profileFragment,
                R.id.mapFragment).build();

        NavigationUI.setupWithNavController(bottom_menu, navController);

        /*
        NavigationBarView bottom_menu = findViewById(R.id.bottom_navigation);
        bottom_menu.setOnItemSelectedListener(item ->
        {
            if(item.getItemId() == R.id.item_home){
                Log.d(TAG, "home selected");
            }
            if(item.getItemId() == R.id.item_profile){
                Log.d(TAG, "profile selected");
            }
            if(item.getItemId() == R.id.item_map){
                Log.d(TAG, "map selected");
            }
            if(item.getItemId() == R.id.item_community){
                Log.d(TAG, "diary selected");
            }
            if(item.getItemId() == R.id.item_add){
                Log.d(TAG, "add selected");
            }
            return true;
        });
         */

        }
    }

