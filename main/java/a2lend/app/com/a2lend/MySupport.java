package a2lend.app.com.a2lend;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Igbar on 1/27/2018.
 */

 public class  MySupport {

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;
    //--|
    public static ProgressDialog showProgressDialog(Context context , String title , String Message ,int Style) {
            ProgressDialog mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(Message);
            mProgressDialog.setIndeterminate(true);
        return mProgressDialog;
    }
    //--|
    public static boolean hideProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            return true;
        }
        return false;
    }

    public static void showMessageDialog(Context context ,String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }
    //--|
    public static File createFileAboutExternalStorage(String dirName,String fileName){
        File dir = new File(Environment.getExternalStorageDirectory() + "/"+ fileName);
        File file = new File(dir, fileName + "__" +UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
            boolean created = file.createNewFile();
            if(created ==false)
                return null;

            Log.d("MySupport-Storage", "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e("MySupport-Storage", "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }
        return file;
    }

    // //--| firebase Package
    public static Uri getFileUri(Context context,File file){
        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         String firebasePackage = "com.firebase.abo3le.firebasehelloworld.fileprovider";
         return FileProvider.getUriForFile(context, firebasePackage , file);
    }

    public static void RotitColor(final ImageButton but){
        but.setBackgroundColor(Color.RED);
    }
    //--|
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean goToFragment(Fragment fragment, FragmentActivity activity){
       try {
           FragmentManager fragmentManager = activity.getSupportFragmentManager();
           FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
           fragmentTransaction.replace((R.id.fragment),fragment);
           fragmentTransaction.commit();
           if(fragment != new HomeFragment()) {
               HomePageActivity.frameLayout.setVisibility(View.GONE);
               HomePageActivity.layoutButtons.setVisibility(View.GONE);
           }else {
               HomePageActivity.frameLayout.setVisibility(View.VISIBLE);
               HomePageActivity.layoutButtons.setVisibility(View.VISIBLE);
           }
           return true;

       }catch (Exception e ){
           return false;
       }
    }

    public static Location getlocation(FragmentActivity fragmentActivity) {
        Location myLocation = null;
        try {
            LocationManager locationManager = (LocationManager) fragmentActivity.getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(fragmentActivity, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                    return null;
                }
                myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (myLocation == null) {
                    myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
        }
        catch (Exception ex){
        }
        return myLocation;
    }
    //--|
    public static void  launchCamera(FragmentActivity fragmentActivity) {
        final String TAG = "launchCamera";
        Log.d(TAG, "Start");
        //@AfterPermissionGranted(RC_STORAGE_PERMS)
        final String AUTHORITY = "com.firebase.abo3le.firebasehelloworld.fileprovider";
        final String permissionWrite = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        final String rationale_storage_message = "This sample reads images from your camera to demonstrate uploading.";
        final int RC_STORAGE_PERMS =102;

        //region Permissions - Check that we have permission to read images from external storage.
        if (!EasyPermissions.hasPermissions(fragmentActivity, permissionWrite)) {
            EasyPermissions.requestPermissions(fragmentActivity, rationale_storage_message,RC_STORAGE_PERMS, permissionWrite );
            return;
        }
        //endregion

        //region Choose file storage location, must be listed in res/xml/file_paths.xml
        String ImageName = UUID.randomUUID().toString();
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, ImageName + ".jpg");

        try {
            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:"+ Environment.getExternalStorageDirectory() + file.getAbsolutePath() + ":" + created);

            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }
        //endregion

        //region Create content:// URI for file
        Uri mFileUri = FileProvider.getUriForFile(fragmentActivity, AUTHORITY, file);
        //endregion

        // Create and launch the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        fragmentActivity.startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
    }
    //--|
    public static  void  hideKeyBoard(View view,FragmentActivity activity){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
