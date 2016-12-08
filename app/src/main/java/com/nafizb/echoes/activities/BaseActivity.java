package com.nafizb.echoes.activities;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.nafizb.echoes.BaseApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nafiz on 23.08.2016.
 */
public class BaseActivity extends AppCompatActivity {
    BaseApplication app;

    AppCompatActivity activity;
    protected Tracker mTracker;
    AlertDialog.Builder d;
    public ProgressDialog progressDialog;

    public static final String PREFS = "Echoes";
    public void baseInit() {
        app = (BaseApplication) getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        initAlertDialog();
    }
    public void initAlertDialog() {
        d = new AlertDialog.Builder(activity);
    }
}
