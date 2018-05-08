package ashu.porter.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;


public class PickDropPresenter implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    PickDropView pickDropView;
    Context context;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;



    public PickDropPresenter(Context context, PickDropView pickDropView){
        this.context = context;
        this.pickDropView = pickDropView;
    }

    public void buildAPIClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();
    }


    public void onDestroy(){
        pickDropView = null;
    }

    public void onStart(){

    }

    public void onResume(){

    }

    public void onPause(){

    }

    public void onStop(){

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        pickDropView.setGooglePlayClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        pickDropView.nullifyGooglePlayclient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
