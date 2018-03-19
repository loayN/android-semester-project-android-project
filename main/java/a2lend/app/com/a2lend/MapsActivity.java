package a2lend.app.com.a2lend;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private GoogleMap mMap;
    final String permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    final String rationale_location_message = "This sample find location from your phone to Use GoogleMaps.";
    final int RC_LOCATION_PERMS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!EasyPermissions.hasPermissions(this, permissionLocation)) {
            EasyPermissions.requestPermissions(this, rationale_location_message, RC_LOCATION_PERMS, permissionLocation);
            return;
        }
    }

    public void  DrawCoordinateSystem(){
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 0.001), new LatLng(179.99, 0.001))
                .width(10)
                .color(Color.GREEN)
                .geodesic(true));
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 0.001), new LatLng(-179.99, 0.001))
                .width(10)
                .color(Color.GREEN)
                .geodesic(true));
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 0.001), new LatLng(0.001, 179.99))
                .width(10)
                .color(Color.RED)
                .geodesic(true));
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 0.001), new LatLng(0.001,-179.99))
                .width(10)
                .color(Color.RED)
                .geodesic(true));
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 179.99), new LatLng(84.99, 179.99))
                .width(10)
                .color(Color.DKGRAY)
                .geodesic(true));
        mMap.addPolyline(new PolylineOptions()
                .add( new LatLng(0.001, 179.99), new LatLng(-84.99, 179.99))
                .width(10)
                .color(Color.DKGRAY)
                .geodesic(true));

    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap = googleMap;

        DrawCoordinateSystem();


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0.001, 0.001);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Ali")
                .snippet("Click To get Details ")
        );
        marker.showInfoWindow();


        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        //endregion
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                     ActivityCompat.checkSelfPermission(this
                 ,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }



        //Move Camera
        /*   CameraPosition cameraPosition = CameraPosition.builder()
                .target(sydney)
                .zoom(6)
                .bearing(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);
*/


/*

        LatLng sydney1 = new LatLng(32.694739, 35.593991);
        mMap.addMarker(new MarkerOptions()
                .position(sydney1 )
                .title("Igbarea")
                .snippet("Click To get Details ")
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
/*


       */
       // PolygonOptions  PolyLine
       // Polygon polygon = mMap.addPolygon(new PolygonOptions().add(sydney,sydney1,sydney2,sydney3,....));



        final GoogleMap mp = mMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mp.addMarker(new MarkerOptions().position(latLng));
                Toast.makeText(MapsActivity.this,  "|" + latLng.latitude +" \n <-> "+ latLng.longitude , Toast.LENGTH_SHORT).show();
                Log.d("Location ",latLng.latitude +" \n  "+ latLng.longitude);
            }
        });
    }


    /////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        //Todo No Permissions

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}

}
