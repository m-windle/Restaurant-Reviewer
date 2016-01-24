package pragmaticdevelopment.com.pragdevrestaurant;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String restoAddress;
    private String restoName;
    private List<Address> restoLocation;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        restoAddress = getIntent().getStringExtra("address").toString();
        restoName = getIntent().getStringExtra("name");

        if(restoAddress != null || !restoAddress.isEmpty()){

            Geocoder geocoder = new Geocoder(this);
            try {
                restoLocation = geocoder.getFromLocationName(restoAddress, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = restoLocation.get(0);
        }
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng restoLocation =  new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(restoLocation).title(restoName)).showInfoWindow();
        moveToCurrentLocation(restoLocation);
        mMap.setMyLocationEnabled(true);
    }
}
