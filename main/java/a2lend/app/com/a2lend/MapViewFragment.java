package a2lend.app.com.a2lend;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Igbar on 1/23/2018.
 */

public class MapViewFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                DrawCoordinateSystem(googleMap);
            }
        });

        return rootView;
    }
    // Draw Coordinate System by 0.0 - 0.0
    public void  DrawCoordinateSystem(GoogleMap mMap){

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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
