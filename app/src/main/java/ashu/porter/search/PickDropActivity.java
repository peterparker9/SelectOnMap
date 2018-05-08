package ashu.porter.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import ashu.porter.R;
import ashu.porter.adapter.PlaceArrayAdapter;
import ashu.porter.adapter.RecentAdapter;
import ashu.porter.db.RealmController;
import ashu.porter.home.MapsActivity;
import ashu.porter.model.Recent;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static ashu.porter.R.id.main_toolbar;


public class PickDropActivity extends AppCompatActivity implements
        PickDropView, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{


    @BindView(R.id.autoSearch)
    AutoCompleteTextView autoCompleteTextSearch;

    @BindView(R.id.linearSearch)
    LinearLayout linearSearch;

    @BindView(main_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.recycleRecentPlaces)
    RecyclerView recycleRecentPlaces;

    private GoogleApiClient mGoogleApiClient;

    private int from;

    private PlaceArrayAdapter mPlaceArrayAdapter;

    PickDropPresenter pickDropPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLayout();

        pickDropPresenter = new PickDropPresenter(PickDropActivity.this, this);

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                 null);
        autoCompleteTextSearch.setAdapter(mPlaceArrayAdapter);
        recycleRecentPlaces.setLayoutManager(new LinearLayoutManager(this));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 1,this)
                .addConnectionCallbacks(this)
                .build();

        recycleRecentPlaces.setVisibility(View.VISIBLE);

        autoCompleteTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleRecentPlaces.setVisibility(View.GONE);
            }
        });

        autoCompleteTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent resultIntent = new Intent(PickDropActivity.this, MapsActivity.class);
                    resultIntent.putExtra("val", parent.getItemAtPosition(position).toString());
                    resultIntent.putExtra("from", from);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
            }
        });
        Realm realm = Realm.getInstance(this);

        RealmResults<Recent> recentPlaces = realm.allObjects(Recent.class);

        if(!recentPlaces.isEmpty()){
            RecentAdapter recentAdapter = new RecentAdapter(this, recentPlaces);
            recycleRecentPlaces.setAdapter(recentAdapter);
        }
    }

    private void initializeLayout(){
        setContentView(R.layout.activity_pickup);
        ButterKnife.bind(this);
        Intent i = getIntent();
        from = i.getIntExtra("from", 0);
        TextView txtTool = (TextView) myToolbar.findViewById(R.id.txtTool);
        setSupportActionBar(myToolbar);
        setToolbarTitle(from);
        if(from == 0)
            txtTool.setText(R.string.pick_up_header);
        else
            txtTool.setText(R.string.drop_header);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
    }

    private void setToolbarTitle(int n){
        if(n == 0)
            getSupportActionBar().setTitle(R.string.pick_up_tool);
        else
            getSupportActionBar().setTitle(R.string.drop_tool);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
        }
    };



    @Override
    protected void onDestroy() {
        pickDropPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pickDropPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pickDropPresenter.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pickDropPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pickDropPresenter.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
