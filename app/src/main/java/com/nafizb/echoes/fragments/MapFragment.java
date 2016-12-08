package com.nafizb.echoes.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.nafizb.echoes.activities.NavigationActivity;
import com.nafizb.echoes.R;
import com.nafizb.echoes.activities.PlayActivity;
import com.nafizb.echoes.activities.RecordActivity;
import com.nafizb.echoes.models.Records;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Nafiz on 20.11.2016.
 */

public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    NavigationActivity activity;
    SupportMapFragment mapFragment;

    ArrayList<Marker> markers = new ArrayList<Marker>();

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Location myLocation;
    boolean isFocusedOnMe = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        }
        ButterKnife.bind(this, rootView);

        init();
        return rootView;
    }

    @Override
    public void init() {
        baseInit();

        activity = (NavigationActivity) getActivity();

        setHasOptionsMenu(true);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        getRecordList();
    }

    public void getRecordList() {
        app.getRestService().getRecords().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Records>>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("EchoesError", e.getMessage());
                    }

                    @Override
                    public final void onNext(List<Records> response) {
                        updateMarkers(response);
                    }
                });
    }

    public void updateMarkers(List<Records> records) {
        markers.clear();
        for (int i = 0; i < records.size(); i++) {
            addMarker(records.get(i));
        }
    }
    public void addMarker(Records item) {
        IconGenerator mBubbleIconFactory = new IconGenerator(getContext());

        View markerView = LayoutInflater.from(getContext()).inflate(R.layout.marker, null, false);

        mBubbleIconFactory.setContentView(markerView);
        mBubbleIconFactory.setBackground(new ColorDrawable(Color.TRANSPARENT));

        LatLng markerLoc = new LatLng(item.location.getLatitude(), item.location.getLongitude());
        markers.add(mMap.addMarker(new MarkerOptions()
                .snippet(item.id + "/-/" + item.title)
                .position(markerLoc)
                .anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.fromBitmap(mBubbleIconFactory.makeIcon()))));

    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        this.mMap = mMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(this);
        focusOnMe();
    }
    @OnClick(R.id.button_fab)
    void click(View view) {
        Bundle locationBundle = new Bundle();
        locationBundle.putDouble("lat", myLocation.getLatitude());
        locationBundle.putDouble("lon", myLocation.getLongitude());

        Intent intent = new Intent(activity, RecordActivity.class);
        intent.putExtras(locationBundle);

        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(Marker clickedMarker) {
        Bundle bundle = new Bundle();
        bundle.putString("snippet", clickedMarker.getSnippet());

        Intent intent = new Intent(getActivity(), PlayActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
        return false;
    }

    private Location getMyLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    public void focusOnMe() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        if (myLocation == null) {
            getMyLocation();
        }

        CameraPosition.Builder camBuilder = new CameraPosition.Builder().bearing(0);
        if (myLocation != null) {
            camBuilder.target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15);
        } else {

            camBuilder.target(new LatLng(41.0132142, 28.86565653))      // Sets the center of the map to location user
                    .zoom(10);                 // Creates a CameraPosition from the builder
        }
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camBuilder.build()));

    }
    @Override
    public void onLocationChanged(Location location) {
        if (myLocation == null) {
            myLocation = location;
        } else {
            myLocation.set(location);
        }

        if (!isFocusedOnMe) {
            focusOnMe();
            isFocusedOnMe = true;
        }
    }
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second
        mLocationRequest.setFastestInterval(4000); // Update location every second

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GAPI", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("GAPI", "GoogleApiClient connection has failed");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName("Map");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
