package it.unimib.travelhub.ui.travels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import it.unimib.travelhub.R;
import it.unimib.travelhub.adapter.UsersRecyclerAdapter;
import it.unimib.travelhub.model.User;

public class TravelActivity extends AppCompatActivity {

    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        RecyclerView recyclerView = findViewById(R.id.friends_recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ArrayList<User> dataSource = new ArrayList<>();

        dataSource.add(new User("John", 23));
        dataSource.add(new User("Jane", 25));
        dataSource.add(new User("Doe", 30));
        dataSource.add(new User("Smith", 28));
        dataSource.add(new User("Doe", 30));
        dataSource.add(new User("Smith", 28));
        dataSource.add(new User("Doe", 30));
        dataSource.add(new User("Smith", 28));
        dataSource.add(new User("Doe", 30));
        dataSource.add(new User("Smith", 28));


        UsersRecyclerAdapter usersRecyclerAdapter = new UsersRecyclerAdapter(dataSource);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(usersRecyclerAdapter);
        findViewById(R.id.buttonBack).setOnClickListener(v -> this.getOnBackPressedDispatcher().onBackPressed());
    }
}