package ashu.porter.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ashu.porter.model.Cost;
import ashu.porter.model.Serviceable;
import ashu.porter.model.Time;
import ashu.porter.network.NetworkClass;
import ashu.porter.network.NetworkService;
import ashu.porter.utils.FindAddress;
import ashu.porter.utils.PermissionCheck;
import ashu.porter.utils.SetLocation;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by apple on 07/05/18.
 */

public class MapPresenter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MapView mapView;
    private Context context;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private static int flag = 0;
    public static final int STATIC_INTEGER_VALUE = 1;


    public MapPresenter(Context context, MapView mapView){
        this.context = context;
        this.mapView = mapView;

        buildGoogleApiClient();
        isServiceable();
    }

    public void onResume(){
        if(mGoogleApiClient == null)
            buildGoogleApiClient();
    }

    public void onPause(){
        if (mGoogleApiClient.isConnected() && mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void onDestroy(){
        mapView = null;
    }

    public void costTime( LatLng latLng){

        final Map<Double, Integer> val = new HashMap<>();

        Retrofit retrofit = NetworkClass.getClient();

        NetworkService service = retrofit.create(NetworkService.class);

        Observable<Cost> findCost = service.getCost(latLng.latitude, latLng.longitude);

        Observable<Time> findETA = service.getETA(latLng.latitude, latLng.longitude);

        Observable
                .zip(findCost, findETA, new BiFunction<Cost, Time, Object>() {
                    @Override
                    public Object apply(@NonNull Cost cost, @NonNull Time time) throws Exception {
                        val.put(cost.getCost(), time.getEta());
                        return val;
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mapView.populateEstimate(0, 0);
                    }

                    @Override
                    public void onNext(Object o) {
                        Map<Double, Integer> val = (HashMap)o;
                        Map.Entry<Double, Integer> entry = val.entrySet().iterator().next();
                        mapView.populateEstimate(entry.getKey(), entry.getValue());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                SetLocation.setLocation(mMap, context);

            } else {
                //Request Location Permission
                PermissionCheck.checkLocationPermission(context);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            SetLocation.setLocation(mMap, context);
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng midLatLng = mMap.getCameraPosition().target;
                try {
                    if(flag == 0) {
                        String add = FindAddress.returnAddress(context, midLatLng);
                        mapView.fillSource(add);
                        costTime(midLatLng);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void onPermissionResult(int requestCode,
                                   String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PermissionCheck.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        SetLocation.setLocation(mMap, context);
                    }

                } else {
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            else
                buildGoogleApiClient();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newText = data.getStringExtra("val");
                    int from = data.getIntExtra("from", 0);
                    flag = 1;
                    if(from == 0) {
                        mapView.fillSource(newText);
                        try {
                            LatLng latLng = FindAddress.getLocationFromAddress(context, newText);
                            if(latLng != null) {
                                markMap(latLng);
                                costTime(latLng);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        mapView.fillDestination(newText);
                    flag = 0;
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void markMap(LatLng latLng){
        SetLocation.markOnMap(mMap, latLng);
    }

    public void isServiceable(){
        Retrofit retrofit = NetworkClass.getClient();

        final NetworkService service = retrofit.create(NetworkService.class);

        Observable<Serviceable> call = service.getServiceability();

        call.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .delay(20, TimeUnit.SECONDS)
                .repeat()
                .subscribeWith(new Observer<Serviceable>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Serviceable serviceable) {
                        if(!serviceable.isServiceable()) {
                            mapView.showBlocked();
                        }
                        else
                            mapView.showUnblocked();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
