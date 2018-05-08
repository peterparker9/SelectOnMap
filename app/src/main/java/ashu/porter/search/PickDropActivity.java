package ashu.porter.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import ashu.porter.utils.PlaceArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static ashu.porter.R.id.main_toolbar;


public class PickDropActivity extends AppCompatActivity implements PickDropView{


    @BindView(R.id.autoSearch)
    AutoCompleteTextView autoCompleteTextSearch;

    @BindView(R.id.linearSearch)
    LinearLayout linearSearch;

    @BindView(main_toolbar)
    Toolbar myToolbar;

    private GoogleApiClient mGoogleApiClient;

    private int from;

    private PlaceArrayAdapter mPlaceArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLayout();

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                 null);
        autoCompleteTextSearch.setAdapter(mPlaceArrayAdapter);

        autoCompleteTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("val", parent.getItemAtPosition(position).toString());
                    resultIntent.putExtra("from", from);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
            }
        });
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
    public void setGooglePlayClient(GoogleApiClient client) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void nullifyGooglePlayclient() {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }
}
