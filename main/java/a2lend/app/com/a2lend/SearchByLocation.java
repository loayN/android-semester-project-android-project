package a2lend.app.com.a2lend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Igbar on 1/23/2018.
 */

public class SearchByLocation extends Fragment implements EasyPermissions.PermissionCallbacks {
    MapView mMapView;
    private GoogleMap googleMap;
    int googleMapindexType = 0;
    Location Mylocation;
    final int RC_LOCATION_PERMS = 102;
    String permissionLocation = android.Manifest.permission.ACCESS_FINE_LOCATION;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View rootView =inflater.inflate(R.layout.search_by_location,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create
        if(SaveSettingsUser.getLocation() == null){
            MySupport.showMessageDialog(getContext(),"Your location is not known","To be used in the function should be declared your location");
            MySupport.goToFragment(new ProfileUpdateFragment(),getActivity());
        }



        //region init MapView
        mMapView = (MapView) rootView.findViewById(R.id.SearchMap);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setBuildingsEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                // region Permission Location
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (!EasyPermissions.hasPermissions(getActivity(), permissionLocation)) {
                        EasyPermissions.requestPermissions(getActivity()
                                , "This sample find location from your phone to Use GoogleMaps."
                                , 102
                                , permissionLocation);
                        return ;
                    }
                }
                //endregion

                final GoogleMap mp = mMap;
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mp.addMarker(new MarkerOptions().position(latLng));
                        Toast.makeText(getActivity(),  latLng.latitude +"\n"+ latLng.longitude , Toast.LENGTH_SHORT).show();
                        Log.d("Location ",latLng.latitude +" \n  "+ latLng.longitude);
                    }
                });

                googleMap.setMyLocationEnabled(true);
                Location Mylocation = SaveSettingsUser.getLocation();

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng( Mylocation.getLatitude(),Mylocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Home Location").snippet("Home"));

                Mylocation = MySupport.getlocation(getActivity());
                sydney = new LatLng( Mylocation.getLatitude(),Mylocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Location Live").snippet("Now"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        //endregion

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "HomeFragment ", Toast.LENGTH_SHORT).show();

        //region button Change type Maps
        Button uploadButton = (Button)getActivity().findViewById(R.id.mapTypeButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(googleMapindexType ==4)
                    googleMapindexType = 0;
                googleMap.setMapType(++googleMapindexType);
            }
        });
        //endregion

        //region Search By Location Distance
        final TextView searchDistance = (TextView) getActivity().findViewById(R.id.SearchDistance);
        ImageButton SearchByDistanceButton = (ImageButton)getActivity().findViewById(R.id.search_SearchDistance);
        SearchByDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataAccess.SearchByLocation(Mylocation,Integer.parseInt(searchDistance.getText().toString()));
                for(int i = 0 ; i < DataAccess.resulSearchList.size() ;i++ ) {
                    Item item = DataAccess.resulSearchList.get(i);
                    LatLng sydney = new LatLng(
                                item.getLatitude()
                            ,   item.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(sydney).title(item.name).snippet(item.description));
                }

            }
        });
        //endregion

        //CheckPermssion
        // region For check Self Permission - Location
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!EasyPermissions.hasPermissions(getActivity(), permissionLocation)) {
                EasyPermissions.requestPermissions(getActivity()
                        , "This sample find location from your phone to Use GoogleMaps."
                        , RC_LOCATION_PERMS
                        , permissionLocation);
            }
            return ;
        }
        //endregion

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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if(requestCode ==102)
            Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "you Cant Use Functional GPS Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
