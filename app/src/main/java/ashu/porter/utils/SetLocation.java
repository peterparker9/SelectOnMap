package ashu.porter.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by apple on 07/05/18.
 */

public class SetLocation {

    public static void setLocation(GoogleMap map, Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);
        //latitude of location
        double myLatitude = myLocation.getLatitude();
        //longitude og location
        double myLongitude = myLocation.getLongitude();
        LatLng latLng = new LatLng(myLatitude, myLongitude);
        if(latLng != null)
            markOnMap(map, latLng);
    }

    public static void markOnMap(GoogleMap mMap, LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
    }


}
