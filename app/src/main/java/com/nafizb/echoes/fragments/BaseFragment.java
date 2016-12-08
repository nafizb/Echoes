package com.nafizb.echoes.fragments;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.Tracker;
import com.nafizb.echoes.BaseApplication;
import com.nafizb.echoes.R;

/**
 * Created by Nafiz on 23.03.2016.
 */
public abstract class BaseFragment extends Fragment {
    BaseApplication app;
    protected View rootView;
    public ProgressDialog progressDialog;

    public static final String PREFS = "EchoesPref";

    protected SharedPreferences settings;
    protected SharedPreferences.Editor editor;
    protected AlertDialog.Builder d;
    protected Tracker mTracker;

    protected void baseInit() {
        //Inıtialize ui compenents
        d = new AlertDialog.Builder(getActivity());

        app = (BaseApplication) getActivity().getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
    abstract void init();

    protected void setAppbarName(String screenName) {
        if(((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(screenName);
        }
    }
    public boolean onBackPressed()
    {
        return true;
    }

    /*
    public void setNormalToolbar(String title) {
        AppCompatActivity activity = (NavigationActivity) getActivity();

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.tool_bar);
        Toolbar toolbarEmpty = (Toolbar) activity.findViewById(R.id.tool_bar_empty);
        toolbar.setVisibility(View.GONE);
        toolbarEmpty.setVisibility(View.VISIBLE);

        activity.setSupportActionBar(toolbarEmpty);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setTitle(title);
    } */

    public void changeFragment(Fragment newFragment) {
        if(newFragment == null) {
            Log.e("FragmentLoadError", "Fragment is null.");
            return;
        }

        final android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.frame_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

}
