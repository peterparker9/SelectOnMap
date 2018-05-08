package ashu.porter.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import android.support.v7.app.AppCompatActivity;

import ashu.porter.R;
import ashu.porter.model.Recent;
import ashu.porter.utils.FindAddress;
import ashu.porter.search.PickDropActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;


public class
MapsActivity extends AppCompatActivity implements MapView, OnMapReadyCallback{

    @BindView(R.id.txtFrom)
    TextView txtFrom;

    @BindView(R.id.txtDestination)
    TextView txtTo;

    @BindView(R.id.linearCost)
    LinearLayout linearCost;

    @BindView(R.id.txtCost)
    TextView txtCost;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.main_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.linearError)
    LinearLayout linearError;

    SupportMapFragment mapFragment;

    GoogleMap mMap;
    MapPresenter presenter;

    public static final int STATIC_INTEGER_VALUE = 1;
    private static int flag = 0;

    Realm realm;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayouts();
        realm = Realm.getInstance(this);
        presenter = new MapPresenter(MapsActivity.this, this);
        mapFragment.getMapAsync(this);
    }

    private void initializeLayouts(){
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        View mv=findViewById(R.id.map);
        View locationButton = ((View) mv.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    @OnClick(R.id.txtFrom)
    public void clickFrom() {
        Intent intent = new Intent(MapsActivity.this, PickDropActivity.class);
        intent.putExtra("from", 0);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    @OnClick(R.id.txtDestination)
    public void clickTo() {
        Intent intent = new Intent(MapsActivity.this, PickDropActivity.class);
        intent.putExtra("from", 1);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        presenter.onMapReady(googleMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        presenter.onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void fillSource(String address) {
        txtFrom.setText(address);
        copyToDb(address);
    }

    @Override
    public void populateEstimate(double cost, int time) {
        linearCost.setVisibility(View.VISIBLE);
        if(cost == 0 && time == 0){
            txtCost.setText("...");
            txtTime.setText("...");
        }
        else {
            txtCost.setText(cost + "");
            txtTime.setText(time + "");
        }

    }

    @Override
    public void fillDestination(String address) {
        txtTo.setText(address);
        copyToDb(address);
    }

    private void copyToDb(String add){
        realm.beginTransaction();
        Recent recent = new Recent();
        recent.setTitle(add);
        realm.copyToRealmOrUpdate(recent);
        realm.commitTransaction();
    }

    @Override
    public void showBlocked() {
        linearError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUnblocked() {
        linearError.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
