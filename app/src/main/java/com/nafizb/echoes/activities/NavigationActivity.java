package com.nafizb.echoes.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nafizb.echoes.R;
import com.nafizb.echoes.fragments.MapFragment;


public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Fragment fragment = new MapFragment();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.frame_container, fragment).commit();
    }
}
