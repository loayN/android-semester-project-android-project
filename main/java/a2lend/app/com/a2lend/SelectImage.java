package a2lend.app.com.a2lend;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Igbar on 1/23/2018.
 */

public class SelectImage extends Fragment  implements EasyPermissions.PermissionCallbacks {
    // TAG.D PRINT
    private static final String TAG = "Storage#SelectImage";
    //firebase image download url
    private Uri mDownloadUrl = null;
    //fiebase image uri
    private Uri mFileUri = null;
    //
    Uri uriCheesed ;
    private StorageReference mStorageRef;
    // Show List Images
    private GridView gridView;
    // Adapter Images To GridView
    private MyCustomAdapter adapterImages;
    //file uri
    private static final String KEY_FILE_URI = "key_file_uri";
    //download file uri
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_image,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(SaveSettingsUser.getLocation() == null){
            MySupport.showMessageDialog(getContext(),"Your location is not known","To be used in the function should be declared your location");
            MySupport.goToFragment(new ProfileUpdateFragment(),getActivity());
        }



        // Initialize Firebase Storage Ref
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // SetAddapter
        gridView = getActivity().findViewById(R.id.gridView);
        adapterImages = new MyCustomAdapter(launchCameraActivity.fileUri);
        gridView.setAdapter(adapterImages);

        // Todo Delete
        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        //Button.OnClick(Camera Launch)
        // region Camera Launch Botton
        final ImageButton cameraButton = (ImageButton)getActivity().findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Go To Camera", Toast.LENGTH_SHORT).show();
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       Intent intent = new Intent(getActivity(),launchCameraActivity.class);
                       startActivity(intent);
                   }
               }).start();

            }
        });
        //endregion

        //Button.OnClick(GoHome)
        // region goBack Home MyListItems Botton
        ImageButton MyListItemsButton = (ImageButton)getActivity().findViewById(R.id.Home);
        MyListItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent Fragment
                MySupport.goToFragment(new MyListItemsFragment(),getActivity());
            }
        });
        //endregion

        //Button.OnClick(AddItem + Upload Data)
        //region Add item And Upload Data to Server And AddItem To DataBase -> Button
        // Action To Upload To Firebase Server
        ImageButton uploadButton = (ImageButton)getActivity().findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView ItemName = getActivity().findViewById(R.id.ItemName);
                final TextView ItemDescription = getActivity().findViewById(R.id.ItemDiscretion);

                //region validation
                if(ItemName.getText().toString().isEmpty()) {
                    ItemName.setError("Item Name is required");
                    ItemName.requestFocus();
                    return;
                }

                if(ItemDescription.getText().toString().isEmpty()) {
                    ItemDescription.setError("Item Description is required");
                    ItemDescription.requestFocus();

                    return;
                }

                if(launchCameraActivity.fileUri.size()== 0) {
                    MySupport.RotitColor(cameraButton);
                    return;
                }
                //endregion

                if(uriCheesed==null){
                    Toast.makeText(getActivity(),"Choose One Image OnClick - CheckBox", Toast.LENGTH_SHORT).show();
                    return;
                }

                UploadImageAsyncTask uploadImageAsyncTask = new UploadImageAsyncTask();
                uploadImageAsyncTask.doInBackground(uriCheesed.toString());


                //Upload Image Server
               // uploadFromUri(uriCheesed);

                final String permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION;
                final String rationale_location_message = "This sample find location from your phone to Use GoogleMaps.";
                final int RC_LOCATION_PERMS = 102;

                if (!EasyPermissions.hasPermissions(getActivity(), permissionLocation)) {
                    EasyPermissions.requestPermissions(getActivity(                                                                                                                                                                                                                                                                 ), rationale_location_message, RC_LOCATION_PERMS, permissionLocation);
                    return;
                }
                // ask to eanbel gps
                final LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }

                // GetMyLocation
                Location MyLocation =  MySupport.getlocation(getActivity());

                Item item = new Item();
                item.setName(ItemName.getText().toString());
                item.setDescription(ItemDescription.getText().toString());
                item.setImagesUri(uriCheesed.getLastPathSegment().toString());
                item.setLatitude( MyLocation.getLatitude());
                item.setLongitude( MyLocation.getLongitude());
                item.setTimeAddItem(new Date().getTime()+"");

                // Add Object Server
                DataAccess.AddObject(item);

                Toast.makeText(getActivity(), "succeed", Toast.LENGTH_SHORT).show();
                launchCameraActivity.fileUri.clear();
                //  MySupport.hideProgressDialog(progressDialog);

                // Intent Fragment
                MySupport.goToFragment(new MyListItemsFragment(),getActivity());

            }
        });
        //endregion

    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
            adapterImages.notifyDataSetChanged();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        //Todo No Permissions

    }

    // Upload File To firebase
    private void uploadFromUri(Uri fileUri) {

        // Toast.makeText(getActivity(), fileUri.toString(), Toast.LENGTH_SHORT).show();

        final String m_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        Log.d("PathImage",m_path);
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("photos").child(fileUri.getLastPathSegment());


        //region Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload succeeded
                Log.d(TAG, "uploadFromUri:onSuccess");

                // Get the public download URL
                mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                // Change The Uri Local Phone with Uri Server ;
                // To Save Uri Server with the Object Item
                //if(i<launchCameraActivity.fileUri.size())
                //     launchCameraActivity.fileUri.set(i, mDownloadUrl);

                Log.d(TAG, "mDownloadUrl:" + mDownloadUrl);

                // Toast.makeText(getActivity(),"onSuccess", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                        //  Toast.makeText(getActivity(), "Error: upload failed", Toast.LENGTH_SHORT).show();

                    }
                });
        //endregion [END upload_from_uri]

    }
    //Show Message No Gps
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("your GPS is Off you have to  run it before complete this process  ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //List Adapter
    public class MyCustomAdapter extends BaseAdapter {

        public  List<Uri>  listItems ;

        public MyCustomAdapter(List<Uri>  listItems) {
            this.listItems=listItems;
        }

        @Override
        public int getCount() {
            return this.listItems.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final  int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater = getLayoutInflater();

            if(position< launchCameraActivity.fileUri.size()) {
                final Uri imageItemUri = listItems.get(position);

                View view = mInflater.inflate(R.layout.image_item,null);

                ImageView image = view.findViewById(R.id.imageViewItem);
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uriCheesed = listItems.get(position);
                    }
                });
                image.setImageURI(imageItemUri);
                return view;
            }
            return null;
        }
    }


    public class UploadImageAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        //    Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();
            Log.d("onPreExecute",s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Toast.makeText(getActivity(),"Start Upload File To Server", Toast.LENGTH_SHORT).show();
            Log.d("onPostExecute","Start");

        }

        @Override
        protected String doInBackground(String... strings) {

             Uri fileUri =Uri.parse(strings[0]);

            final String m_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

            Log.d("PathImage",m_path);
            Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

            // Get a reference to store file at photos/<FILENAME>.jpg
            final StorageReference photoRef = mStorageRef.child("photos").child(fileUri.getLastPathSegment());


            //region Upload file to Firebase Storage
            Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
            photoRef.putFile(fileUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Upload succeeded
                    Log.d(TAG, "uploadFromUri:onSuccess");

                    // Get the public download URL
                    mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

                    Log.d(TAG, "mDownloadUrl:" + mDownloadUrl);

                    // Toast.makeText(getActivity(),"onSuccess", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Upload failed
                    Log.w(TAG, "uploadFromUri:onFailure", exception);

                    mDownloadUrl = null;
                    //  Toast.makeText(getActivity(), "Error: upload failed", Toast.LENGTH_SHORT).show();

                }
            });

            if(mDownloadUrl == null) {
                return "Failure";
            }

            return "Success - Upload File ";
        }
    }

}
