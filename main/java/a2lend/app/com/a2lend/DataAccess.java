package a2lend.app.com.a2lend;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Igbar on 12/18/2017.
 */

public class DataAccess {
    // User Firebase
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    // get Instance Database
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    // Reference Database
    public static DatabaseReference databaseReference = database.getReference();
    // item Reference
    public static DatabaseReference ReferenceItems = databaseReference.child("items");
    // StorageReference
    public static StorageReference mStorageRefPhotos = FirebaseStorage.getInstance().getReference().child("photos");

    public static List<Item> myListItems = new ArrayList<Item>();

    public static List<Item> resulSearchList = new ArrayList<Item>();

    public DataAccess(){

    }

    public static FirebaseUser getUser(){
        return auth.getCurrentUser();
    }
    public static boolean isAnonymous(){
        return auth.getCurrentUser().isAnonymous();
    }
    public static String getUserId(){
        auth.getCurrentUser().reload();
        return auth.getCurrentUser().getUid();
    }
    public static void updateEmail(String email) {
        auth.getCurrentUser().reload();
        auth.getCurrentUser().updateEmail(email);
    }
    public static void updatePassword(String password) {
        auth.getCurrentUser().reload();
        auth.getCurrentUser().updatePassword(password);
    }
    public static  String getEmail(String password) {
        auth.getCurrentUser().reload();
        return auth.getCurrentUser().getEmail();
    }
    public static void updatePhoneNumber(PhoneAuthCredential credential){
        auth.getCurrentUser().reload();
        auth.getCurrentUser().updatePhoneNumber(credential);
    }

    public static void sendPasswordResetEmail(final Context context, String emailAddress){

        auth.sendPasswordResetEmail(emailAddress)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        MySupport.showMessageDialog(context,"Send Password Reset Email","Success");
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MySupport.showMessageDialog(context,"Send Password Reset Email","Failure");

                    }
                });


    }

    public static void UpdateUser( String Name,String Email , String Password , String Phone ){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates;

        if(!Name.isEmpty()) {
            profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(Name).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("UpdateUser", "User profile updated.");
                    }
                }
            });
        }

        if(!Email.isEmpty())
            auth.getCurrentUser().updateEmail(Email);
        if(!Password.isEmpty())
             auth.getCurrentUser().updatePassword(Password);
    }

    public static void AddObject(Item item){
        String Random_Key = ReferenceItems.push().getKey();
        item.setUser(getUserId());
        item.setId(Random_Key);
        if(!isAnonymous())
            ReferenceItems.child(item.user+"_"+Random_Key).setValue(item);
        if(!myListItems.contains(item))
            myListItems.add(item);
    }

    public static boolean deleteItem(Item item) {
        String TAG = "DataAccess#deleteItem";
        // check if the item is exist
        if(ReferenceItems.child(item.getUser()+"_"+item.id)==null){
            Log.w(TAG,"Item is not Exist : - "+ item.getId());
            return false;
        }

        //remove item
        ReferenceItems.child(item.getUser()+"_"+item.id).removeValue();
        Log.w(TAG,"Remove Item : - "+ item.getId());

        if(mStorageRefPhotos.child(item.getImagesUri()) ==null)
            Log.w(TAG,"Image is not Exist : - "+ item.getId());

        mStorageRefPhotos.child(item.getImagesUri()).delete();
        Log.w(TAG,"Remove Image : - "+ item.getImagesUri());
        return true;
    }

    public static boolean updateObject(Item item) {
        String TAG = "DataAccess#updateItem";

        // check if the item is exist
        if(ReferenceItems.child(item.getUser()+"_"+item.id)==null){
            Log.w(TAG,"Item is not Exist : - "+ item.getId());
            return false;
        }

        //Update Item
         ReferenceItems.child(item.getUser()+"_"+item.id).setValue(item);
         Log.w(TAG,"Update Item : - "+ item.getId());
        return true;
    }

    public static List<Item> UpdateMyListItems(){
        if(isAnonymous())
            return null;

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> posts = dataSnapshot.getChildren();
                for(DataSnapshot post : posts){
                    Item item = post.getValue(Item.class);
                    if(item.user.equals(getUserId())&& !myListItems.contains(item))
                        myListItems.add(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("getMyItems", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        Query query = ReferenceItems;
        query.addValueEventListener(postListener);

        return myListItems;
    }

    public static List<Item> SearchByLocation(final Location location, final double byDistance){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> posts = dataSnapshot.getChildren();
                for(DataSnapshot post : posts){
                    Item item = post.getValue(Item.class);
                    double distance = Math.hypot(
                             item.getLatitude() - location.getLatitude()
                            ,item.getLongitude()- location.getLongitude());

                    distance= Math.abs(distance);

                    if(distance <byDistance)
                        resulSearchList.add(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("getMyItems", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Query query = ReferenceItems;
        query.addValueEventListener(postListener);
        return resulSearchList;
    }

    public static List<Item> SearchByName(final String Name){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> items = dataSnapshot.getChildren();
                Item foundItemResult;
                for(DataSnapshot item : items){
                    String name = item.child("name").getValue().toString();
                    if(name.equals(Name)){
                        foundItemResult = item.getValue(Item.class);
                        resulSearchList.add(foundItemResult);
                    }

                    //  DataSnapshot DataSnapshotItem = item;
                    //  Iterable<DataSnapshot> result = DataSnapshotItem.getChildren();
                    //    result.
                    //   for(DataSnapshot i : result){
                    //    if(i.getKey().equals("name")){
                    //          Log.w("LocationObject",post1.getKey());
                    //          l = post1.getValue(Location.class);
                    //      }
                    //      Log.w("Object","key : "+post1.getKey().toString()+"\nValue:"+post1.getValue() );
                    //   Log.w("Object ",item.name +" " +item.description + "\n" );
                   // resulSearch.add(item);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("getMyItems", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        Query query = ReferenceItems;
        query.addValueEventListener(postListener);
        Log.d("SearchByLocation", "number Items Found "+resulSearchList.size()+ " ");
        return resulSearchList;
    }

    public static List<Item> getItems(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> posts = dataSnapshot.getChildren();
                for(DataSnapshot post : posts){
                    Item item = post.getValue(Item.class);
                    myListItems.add(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("getMyItems", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Query query = ReferenceItems.limitToFirst(10);
        query.addValueEventListener(postListener);
        return myListItems;
    }

    public static void uploadFromUri(Uri fileUri , FragmentActivity fragmentActivity) {
        final String TAG = "uploadFromUri";

        //final String m_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRefPhotos.child(fileUri.getLastPathSegment());

        //firebase image download url
        final Uri[] mDownloadUrl = {null};

        //region Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri).addOnSuccessListener(fragmentActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Upload succeeded
                Log.d(TAG, "uploadFromUri:onSuccess");

                // Get the public download URL
                mDownloadUrl[0] = taskSnapshot.getMetadata().getDownloadUrl();
                // Change The Uri Local Phone with Uri Server ;
                // To Save Uri Server with the Object Item

                //if(i<launchCameraActivity.fileUri.size())
                //     launchCameraActivity.fileUri.set(i, mDownloadUrl);
                Log.d(TAG, "mDownloadUrl:" + mDownloadUrl[0]);
            }
        }).addOnFailureListener(fragmentActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Upload failed
                mDownloadUrl[0] = null;
                Log.w(TAG, "uploadFromUri:onFailure", exception);
            }
        });
        //endregion [END upload_from_uri]

    }


    private class MyTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            ReferenceItems.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                   // LoadData(dataSnapshot);
                    String userID = dataSnapshot.child("user").getValue(String.class);
                    if(userID.equals(getUserId()))
                    {
                        Item item =dataSnapshot.getValue(Item.class);
                        myListItems.add(item);
                    }
                    MyListItemsFragment.adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String userID = dataSnapshot.child("user").getValue(String.class);
                    if(userID.equals(getUserId()))
                    {
                        Item item =dataSnapshot.getValue(Item.class);
                        for(Item i : myListItems) {
                            if( i.getId().equals(item.id)){
                              myListItems.remove(i);
                              myListItems.add(item);
                            }
                        }
                    }
                    MyListItemsFragment.adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String itemId = dataSnapshot.child("id").getValue(String.class);
                        for(Item i : myListItems) {
                            if( i.getId().equals(itemId)){
                                myListItems.remove(i);
                            }
                        }
                    MyListItemsFragment.adapter.notifyDataSetChanged();
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }


    }



}
