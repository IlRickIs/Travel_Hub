package it.unimib.travelhub.ui.main;

import static it.unimib.travelhub.util.Constants.LAST_UPDATE;
import static it.unimib.travelhub.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import it.unimib.travelhub.R;
import it.unimib.travelhub.databinding.ActivityMainBinding;
import it.unimib.travelhub.databinding.ActivityTravelBinding;
import it.unimib.travelhub.ui.profile.ProfileFragmentAdapter;
import it.unimib.travelhub.ui.travels.AddTravelActivity;
import it.unimib.travelhub.util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainFragmentAdapter myFragmentAdapter;

    private ActivityMainBinding binding;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView bottom_menu = findViewById(R.id.bottom_navigation);
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
//                findFragmentById(R.id.nav_host_fragment);
//        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
//        NavigationUI.setupWithNavController(bottom_menu, navController);
//        bottom_menu.setOnItemSelectedListener(item -> {
//            if(item.getItemId() == R.id.homeFragment){
//                navController.navigate(R.id.homeFragment);
//                findViewById(R.id.add_travel_menu).setVisibility(View.GONE);
//                return true;
//            }
//            else if(item.getItemId() == R.id.profileFragment){
//                navController.navigate(R.id.profileFragment);
//                findViewById(R.id.add_travel_menu).setVisibility(View.GONE);
//                return true;
//            } else if(item.getItemId() == R.id.mapFragment){
//                navController.navigate(R.id.mapFragment);
//                findViewById(R.id.add_travel_menu).setVisibility(View.GONE);
//                return true;
//            } else if(item.getItemId() == R.id.communityFragment){
//                navController.navigate(R.id.communityFragment);
//                findViewById(R.id.add_travel_menu).setVisibility(View.GONE);
//                return true;
//            } else if(item.getItemId() == R.id.addFragment){
//                findViewById(R.id.add_travel_menu).setVisibility(View.VISIBLE);
//                return true;
//            }
//            return false;
//        });

        binding.viewPagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });

        Log.d(TAG, "PIPPO: " + binding.viewPagerMain);

        bottom_menu.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.homeFragment) {
                binding.viewPagerMain.setCurrentItem(0);
                return true;
            } else if(item.getItemId() == R.id.communityFragment){
                binding.viewPagerMain.setCurrentItem(1);
                return true;
            } else if(item.getItemId() == R.id.mapFragment){
                binding.viewPagerMain.setCurrentItem(2);
                return true;
            } else if(item.getItemId() == R.id.profileFragment){
                binding.viewPagerMain.setCurrentItem(3);
                return true;
            }
            return false;
        });


        findViewById(R.id.fab_add_travel).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTravelActivity.class);
            startActivity(intent);
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        myFragmentAdapter = new MainFragmentAdapter(fragmentManager, getLifecycle());
        binding.viewPagerMain.setAdapter(myFragmentAdapter);

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

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getApplication());
        sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE, "0");
        Log.d("MainActivity", "Last update deleted: " + sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE));
    }
}

