package it.unimib.travelhub.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationBarView;

import it.unimib.travelhub.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            if(item.getItemId() == R.id.item_diary){
                Log.d(TAG, "diary selected");
            }
            return true;
        });

        }
    }

