package com.example.myswimming;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, PlacesListener{


    private static final String TAG = "@@@";

    private GoogleApiClient mGoogleApiClient = null;

    private LocationRequest mLocationRequest;

    private static final int REQUEST_CODE_LOCATION = 2000;//임의의 정수로 정의

    private static final int REQUEST_CODE_GPS = 2001;//임의의 정수로 정의

    private GoogleMap googleMap;

    LocationManager locationManager;

    MapFragment mapFragment;

    boolean setGPS = false;

    LatLng SEOUL = new LatLng(37.56, 126.97);

    LatLng currentPosition;

    Marker current_marker =null;

    List<Marker> previous_marker = null;



    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(this)

                .addOnConnectionFailedListener(this)

                .addApi(LocationServices.API)

                .build();



        mGoogleApiClient.connect();





    }





    //GPS 활성화를 위한 다이얼로그 보여주기

    private void showGPSDisabledAlertToUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("GPS가 비활성화 되어있습니다. 활성화 할까요?")

                .setCancelable(false)

                .setPositiveButton("설정", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_GPS);

                    }

                });



        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();

            }

        });

        AlertDialog alert = alertDialogBuilder.create();

        alert.show();

    }





    //GPS 활성화를 위한 다이얼로그의 결과 처리

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        switch (requestCode) {

            case REQUEST_CODE_GPS:

                //Log.d(TAG,""+resultCode);

                //if (resultCode == RESULT_OK)

                //사용자가 GPS 활성 시켰는지 검사

                if (locationManager == null)

                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    // GPS 가 ON으로 변경되었을 때의 처리.

                    setGPS = true;



                    mapFragment.getMapAsync(MapsActivity.this);

                }

                break;

        }

    }



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        mapFragment = (MapFragment) getFragmentManager()

                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);



        previous_marker = new ArrayList<Marker>();



        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {



                googleMap.clear();//지도 클리어



                if ( previous_marker != null)

                    previous_marker.clear();//지역정보 마커 클리어



                new NRPlaces.Builder()

                        .listener(MapsActivity.this)

                        .key("AIzaSyCmMRpwiE17GaN6IB5iFvA3yfjm3QLLXp4")

                        .latlng(currentPosition.latitude, currentPosition.longitude)//현재 위치

                        .radius(500) //500 미터 내에서 검색

                        .type(PlaceType.RESTAURANT) //음식점

                        .build()

                        .execute();

            }

        });

    }





    public boolean checkLocationPermission()

    {

        Log.d( TAG, "checkLocationPermission");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {



                //퍼미션 요청을 위해 UI를 보여줘야 하는지 검사

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block

                    // this thread waiting for the user's response! After the user

                    // sees the explanation, try again to request the permission.



                    //Prompt the user once explanation has been shown;

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);



                } else

                    //UI보여줄 필요 없이 요청

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);



                return false;

            } else {



                Log.d( TAG, "checkLocationPermission"+"이미 퍼미션 획득한 경우");



                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)

                {

                    Log.d(TAG, "checkLocationPermission Version >= M");

                    showGPSDisabledAlertToUser();

                }



                if (mGoogleApiClient == null) {

                    Log.d( TAG, "checkLocationPermission "+"mGoogleApiClient==NULL");

                    buildGoogleApiClient();

                }

                else  Log.d( TAG, "checkLocationPermission "+"mGoogleApiClient!=NULL");



                if ( mGoogleApiClient.isConnected() ) Log.d( TAG, "checkLocationPermission"+"mGoogleApiClient 연결되 있음");

                else Log.d( TAG, "checkLocationPermission"+"mGoogleApiClient 끊어져 있음");





                mGoogleApiClient.reconnect();//이미 연결되 있는 경우이므로 다시 연결



                googleMap.setMyLocationEnabled(true);

            }

        }

        else {

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)

            {

                Log.d(TAG, "checkLocationPermission Version < M");

                showGPSDisabledAlertToUser();

            }



            if (mGoogleApiClient == null) {

                buildGoogleApiClient();

            }

            googleMap.setMyLocationEnabled(true);

        }



        return true;

    }



    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_LOCATION: {



                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                {

                    //퍼미션이 허가된 경우

                    if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))

                    {



                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)

                        {

                            Log.d(TAG, "onRequestPermissionsResult");

                            showGPSDisabledAlertToUser();

                        }





                        if (mGoogleApiClient == null) {

                            buildGoogleApiClient();

                        }

                        googleMap.setMyLocationEnabled(true);

                    }

                } else {

                    Toast.makeText(this, "퍼미션 취소", Toast.LENGTH_LONG).show();

                }

                return;

            }

        }

    }



    @Override

    public void onMapReady(GoogleMap map)

    {

        googleMap = map;



        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));





        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {



            @Override

            public void onMapLoaded() {

                Log.d( TAG, "onMapLoaded" );



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

                {

                    checkLocationPermission();

                }

                else

                {



                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !setGPS)

                    {

                        Log.d(TAG, "onMapLoaded");

                        showGPSDisabledAlertToUser();

                    }



                    if (mGoogleApiClient == null) {

                        buildGoogleApiClient();

                    }

                    googleMap.setMyLocationEnabled(true);

                }



            }

        });





        //구글 플레이 서비스 초기화

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

        {

            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                    || ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

            {

                buildGoogleApiClient();



                googleMap.setMyLocationEnabled(true);

            }

            else

            {

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }

        }

        else

        {

            buildGoogleApiClient();

            googleMap.setMyLocationEnabled(true);

        }

    }





    //성공적으로 GoogleApiClient 객체 연결되었을 때 실행

    @Override

    public void onConnected(@Nullable Bundle bundle) {

        Log.d( TAG, "onConnected" );



        if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

            setGPS = true;



        mLocationRequest = new LocationRequest();

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationRequest.setInterval(1000);

        mLocationRequest.setFastestInterval(1000);





        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                || ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {



            Log.d( TAG, "onConnected " + "getLocationAvailability mGoogleApiClient.isConnected()="+mGoogleApiClient.isConnected() );

            if ( !mGoogleApiClient.isConnected()  ) mGoogleApiClient.connect();





            // LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);



            if ( setGPS && mGoogleApiClient.isConnected() )//|| locationAvailability.isLocationAvailable() )

            {

                Log.d( TAG, "onConnected " + "requestLocationUpdates" );

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if ( location == null ) return;



                //현재 위치에 마커 생성

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title("현재위치");

                googleMap.addMarker(markerOptions);



                //지도 상에서 보여주는 영역 이동

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }



        }



    }





    @Override

    public void onConnectionFailed(ConnectionResult result) {

        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

    }



    @Override

    public void onConnectionSuspended(int cause) {

        //구글 플레이 서비스 연결이 해제되었을 때, 재연결 시도

        Log.d(TAG, "Connection suspended");

        mGoogleApiClient.connect();

    }



    @Override

    protected void onStart() {

        super.onStart();



        if (mGoogleApiClient != null)

            mGoogleApiClient.connect();

    }



    @Override

    public void onResume() {

        super.onResume();

        if (mGoogleApiClient != null)

            mGoogleApiClient.connect();

    }



    @Override

    protected void onStop() {



        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            mGoogleApiClient.disconnect();

        }

        super.onStop();

    }





    @Override

    public void onPause() {

        if ( mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            mGoogleApiClient.disconnect();

        }



        super.onPause();

    }



    @Override

    protected void onDestroy() {



        Log.d( TAG, "OnDestroy");



        if (mGoogleApiClient != null) {

            mGoogleApiClient.unregisterConnectionCallbacks(this);

            mGoogleApiClient.unregisterConnectionFailedListener(this);



            if (mGoogleApiClient.isConnected()) {

                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            }



            mGoogleApiClient.disconnect();

            mGoogleApiClient = null;

        }



        super.onDestroy();

    }





    @Override

    public void onLocationChanged(Location location) {



        currentPosition = new LatLng( location.getLatitude(), location.getLongitude() );

        String errorMessage = "";



        if (current_marker != null )

            current_marker.remove();



        //현재 위치에 마커 생성

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);

        markerOptions.title("현재위치");

        current_marker = googleMap.addMarker(markerOptions);



        //지도 상에서 보여주는 영역 이동

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.getUiSettings().setCompassEnabled(true);





        //지오코더... GPS를 주소로 변환

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());



        // Address found using the Geocoder.

        List<Address> addresses = null;



        try {

            // Using getFromLocation() returns an array of Addresses for the area immediately

            // surrounding the given latitude and longitude. The results are a best guess and are

            // not guaranteed to be accurate.

            addresses = geocoder.getFromLocation(

                    location.getLatitude(),

                    location.getLongitude(),

                    // In this sample, we get just a single address.

                    1);

        } catch (IOException ioException) {

            // Catch network or other I/O problems.

            errorMessage = "지오코더 서비스 사용불가";

            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();

        } catch (IllegalArgumentException illegalArgumentException) {

            // Catch invalid latitude or longitude values.

            errorMessage = "잘못된 GPS 좌표";

            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();



        }



        // Handle case where no address was found.

        if (addresses == null || addresses.size()  == 0) {

            if (errorMessage.isEmpty()) {

                errorMessage = "주소 미발견";

                Log.e(TAG, errorMessage);

            }

            Toast.makeText( this, errorMessage, Toast.LENGTH_LONG).show();

        } else {

            Address address = addresses.get(0);

          //****************  Toast.makeText( this, address.getAddressLine(0).toString(), Toast.LENGTH_LONG).show();

        }

    }





    @Override

    public void onPlacesFailure(PlacesException e) {

        Log.i("PlacesAPI", "onPlacesFailure()");

    }



    @Override

    public void onPlacesStart() {

        Log.i("PlacesAPI", "onPlacesStart()");

    }



    @Override

    public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {

        Log.i("PlacesAPI", "onPlacesSuccess()");



        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                for (noman.googleplaces.Place place : places) {



                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());



                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);

                    markerOptions.title(place.getName());

                    markerOptions.snippet(place.getVicinity());

                    Marker item = googleMap.addMarker(markerOptions);

                    previous_marker.add(item);



                }



                //중복 마커 제거

                HashSet<Marker> hashSet = new HashSet<Marker>();

                hashSet.addAll(previous_marker);

                previous_marker.clear();

                previous_marker.addAll(hashSet);



            }

        });

    }



    @Override

    public void onPlacesFinished() {

        Log.i("PlacesAPI", "onPlacesFinished()");

    }

}