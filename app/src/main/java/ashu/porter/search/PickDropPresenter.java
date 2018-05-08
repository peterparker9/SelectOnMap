package ashu.porter.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;


public class PickDropPresenter{

    PickDropView pickDropView;
    Context context;

    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 10;



    public PickDropPresenter(Context context, PickDropView pickDropView){
        this.context = context;
        this.pickDropView = pickDropView;
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

}
