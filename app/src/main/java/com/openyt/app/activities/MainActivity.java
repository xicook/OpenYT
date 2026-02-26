package com.openyt.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openyt.app.R;
import com.openyt.app.fragments.HomeFragment;
import com.openyt.app.fragments.SearchFragment;
import com.openyt.app.fragments.UpdatesFragment;

/**
 * Tela principal do OpenYT.
 * Gerencia as 3 abas: Início, Pesquisar e Updates.
 */
public class MainActivity extends AppCompatActivity {

    private static final int TAB_HOME    = 0;
    private static final int TAB_SEARCH  = 1;
    private static final int TAB_UPDATES = 2;

    private int currentTab = TAB_HOME;

    // Bottom nav
    private LinearLayout tabHome, tabSearch, tabUpdates;
    private ImageView iconHome, iconSearch, iconUpdates;

    // Toolbar
    private TextView toolbarTitle;
    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupBottomNav();

        // Abre a aba inicial (Home)
        if (savedInstanceState == null) {
            openTab(TAB_HOME);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        btnSearch    = toolbar.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTab(TAB_SEARCH);
            }
        });
    }

    private void setupBottomNav() {
        tabHome    = findViewById(R.id.tab_home);
        tabSearch  = findViewById(R.id.tab_search);
        tabUpdates = findViewById(R.id.tab_updates);
        iconHome    = findViewById(R.id.icon_home);
        iconSearch  = findViewById(R.id.icon_search);
        iconUpdates = findViewById(R.id.icon_updates);

        tabHome.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openTab(TAB_HOME); }
        });
        tabSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openTab(TAB_SEARCH); }
        });
        tabUpdates.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openTab(TAB_UPDATES); }
        });
    }

    private void openTab(int tab) {
        currentTab = tab;
        Fragment fragment;

        switch (tab) {
            case TAB_SEARCH:
                fragment = new SearchFragment();
                toolbarTitle.setText(R.string.nav_search);
                break;
            case TAB_UPDATES:
                fragment = new UpdatesFragment();
                toolbarTitle.setText(R.string.nav_updates);
                break;
            default:
                fragment = new HomeFragment();
                toolbarTitle.setText(R.string.app_name);
                break;
        }

        // Troca o fragmento
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        updateBottomNavColors(tab);
    }

    /** Atualiza as cores da bottom nav para mostrar qual aba está ativa */
    private void updateBottomNavColors(int activeTab) {
        int active   = getResources().getColor(R.color.bottomNavActive);
        int inactive = getResources().getColor(R.color.bottomNavInactive);

        iconHome.setColorFilter(activeTab == TAB_HOME     ? active : inactive);
        iconSearch.setColorFilter(activeTab == TAB_SEARCH ? active : inactive);
        iconUpdates.setColorFilter(activeTab == TAB_UPDATES ? active : inactive);

        // Atualiza as TextViews dos tabs também
        ((TextView) ((LinearLayout) tabHome).getChildAt(1))
                .setTextColor(activeTab == TAB_HOME ? active : inactive);
        ((TextView) ((LinearLayout) tabSearch).getChildAt(1))
                .setTextColor(activeTab == TAB_SEARCH ? active : inactive);
        ((TextView) ((LinearLayout) tabUpdates).getChildAt(1))
                .setTextColor(activeTab == TAB_UPDATES ? active : inactive);
    }
}
