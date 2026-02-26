package com.openyt.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import com.openyt.app.R;
import com.openyt.app.fragments.HomeFragment;
import com.openyt.app.fragments.SearchFragment;
import com.openyt.app.fragments.UpdatesFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navSearch, navUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navHome    = findViewById(R.id.nav_home);
        navSearch  = findViewById(R.id.nav_search);
        navUpdates = findViewById(R.id.nav_updates);

        navHome.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navSearch.setOnClickListener(v -> loadFragment(new SearchFragment()));
        navUpdates.setOnClickListener(v -> loadFragment(new UpdatesFragment()));

        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
}
