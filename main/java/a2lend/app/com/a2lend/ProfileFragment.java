package a2lend.app.com.a2lend;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Igbar on 1/23/2018.
 */

public class ProfileFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    MapView mMapView;
    private GoogleMap googleMap;

    final int RC_LOCATION_PERMS = 102;
    String permissionLocation = android.Manifest.permission.ACCESS_FINE_LOCATION;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create


        //region Map Settings Inint
        mMapView = (MapView) rootView.findViewById(R.id.mapProfile);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // Buttons Zoom in/out
                googleMap.setBuildingsEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                //To Change Type to MAP_TYPE_HYBRID
                //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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

                googleMap.setMyLocationEnabled(true);

                // region get My Location
                Location location = MySupport.getlocation(getActivity());
                if(location==null){
                    Toast.makeText(getActivity(), "Run Location Gps ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //endregion

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng( location.getLatitude(),location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(sydney).title("You Are Location").snippet("Now"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(10).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });
        //endregion

        return rootView;
        //mapProfile
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  created

        // Title Page
        TextView user_profile_name = (TextView) getActivity().findViewById(R.id.user_profile_name);
        TextView PemailViewText = (TextView) getActivity().findViewById(R.id.PemailViewText);

        // User Information
        TextView name = (TextView) getActivity().findViewById(R.id.PuserName);
        TextView email = (TextView) getActivity().findViewById(R.id.PEmail);
        TextView phone = (TextView) getActivity().findViewById(R.id.PNumber);

        //region Get User FirebaseUser Correct
        FirebaseUser user = DataAccess.getUser();
        if(!user.isAnonymous()) {
            user_profile_name.setText(user.getDisplayName());
            PemailViewText.setText(user.getEmail());
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            phone.setText(user.getPhoneNumber());
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}

