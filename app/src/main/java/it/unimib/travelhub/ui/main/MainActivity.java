package it.unimib.travelhub.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

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
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(bottom_menu, navController);

        bottom_menu.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.homeFragment){
                navController.navigate(R.id.homeFragment);
                return true;
            }
            else if(item.getItemId() == R.id.profileFragment){
                navController.navigate(R.id.profileFragment);
                return true;
            } else if(item.getItemId() == R.id.mapFragment){
                navController.navigate(R.id.mapFragment);
                return true;
            } else if(item.getItemId() == R.id.communityFragment){
                navController.navigate(R.id.communityFragment);
                return true;
            } else if(item.getItemId() == R.id.addFragment){
                navController.navigate(R.id.addFragment);
                return true;
            }
            return false;
        });
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

