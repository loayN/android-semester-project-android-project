package a2lend.app.com.a2lend;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Date;
import java.util.List;


/**
 * Created by Igbar on 1/22/2018.
 */

public class MyListItemsFragment extends Fragment {
    boolean flagRefreshList = true;
    static MyCustomAdapter adapter;
    ListView listv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_list_items,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  created





        listv = (ListView)view.findViewById(R.id.list);
        listv.clearAnimation();

        if(DataAccess.myListItems.size()==0)
            DataAccess.UpdateMyListItems();

        adapter = new MyCustomAdapter(DataAccess.myListItems); // MyListItems
        listv.setAdapter(adapter);

        //region Button Go to AddItem
        ImageButton AddItemButton = (ImageButton)getActivity().findViewById(R.id.AddItem);
        AddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySupport.goToFragment(new SelectImage(),getActivity());
            }
        });
        //endregion

        //region ControlItemsButton
        ImageButton ControlPanelButton = (ImageButton)getActivity().findViewById(R.id.UpdateItems);
        ControlPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();

               // MySupport.goToFragment(new ControlPanelFragment(),getActivity());
            }
        });
        //endregion

        // refresh ListItems to check if have messages - Up date
        //region Handler flagRefreshList
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(flagRefreshList) {
                    adapter.notifyDataSetChanged();
                    flagRefreshList = false;
                }

            }
        }, 1000);
        //endregion Handler flagRefreshList

    }

    public class MyCustomAdapter extends BaseAdapter {

        public  List<Item>  listItems ;

        public MyCustomAdapter(List<Item>  listItems) {
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
        public View getView(final  int position,  View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();

            final Item item =listItems.get(position);

            View myView = mInflater.inflate(R.layout.tool_item, null);

            TextView name = (TextView) myView.findViewById(R.id.ViewItemName);
            name.setText(item.name);

            TextView des = (TextView) myView.findViewById(R.id.ViewItemDes);
            des.setText(item.description);

            final ImageView imageView =(ImageView) myView.findViewById(R.id.ImageViewItem);
            // ServerStorage getImage
            StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("photos").child(item.getImagesUri());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(imageView);

            // Set OnClick Image
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowItemDialog(item,imageView.getDrawable());
                }
            });

            //region ShowButton
            ImageButton ShowButton = (ImageButton)myView.findViewById(R.id.itemShow);
            ShowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowItemDialog(item,imageView.getDrawable());
                }
            });
            //endregion

            //region DeleteButton
            ImageButton DeleteButton = (ImageButton)myView.findViewById(R.id.itemDelete);
            DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete entry");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            new AsyncTask<String, String, String>() {
                                @Override
                                protected String doInBackground(String... strings) {
                                    // continue with delete
                                    if(DataAccess.myListItems.remove(item))
                                        Log.d("MyListDelete", "Delete successful");
                                    if(DataAccess.deleteItem(item))
                                        Log.d("MyListDelete", "Delete successful ");

                                    //notify Data Set List Changed
                                    flagRefreshList=true;
                                    return null;
                                }
                            }.execute();

                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alert.show();

                }
            });
            //endregion

            //region UpdateButton
            ImageButton UpdateButton = (ImageButton)myView.findViewById(R.id.itemUpdate);
            UpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateItemDialog(item,imageView.getDrawable());


                }
            });
            //endregion

            return myView;


        }

        private void ShowItemDialog(Item item, Drawable drawable){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(item.name);
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View view = factory.inflate(R.layout.show_item, null);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog  = alertDialogBuilder.show();
            ImageView image= (ImageView) view.findViewById(R.id.ImageViewFullScreen);
            image.setImageDrawable(drawable);
         /*   StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("photos").child(item.getImagesUri());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(image);*/


            TextView des= (TextView) view.findViewById(R.id.disItemDialog);
            des.setText(item.description);


            ImageButton BackButton = (ImageButton) view.findViewById(R.id.BackDialog);
            if(item.user.equals(DataAccess.getUserId())) {
                BackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Edit Button
                        alertDialog.dismiss();
                    }
                });
            }else{
                BackButton.setVisibility(View.GONE);
            }
        }

        private void UpdateItemDialog(final Item item,Drawable drawable){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Update");
            LayoutInflater factory = LayoutInflater.from(getContext());
            View view = factory.inflate(R.layout.update_item, null);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog  = alertDialogBuilder.show();

            ImageView image= (ImageView) view.findViewById(R.id.DialogUpdateItemImage);
            image.setImageDrawable(drawable);

           /* StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("photos").child(item.getImagesUri());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(image);
            */
            final EditText name = (EditText) view.findViewById(R.id.DialogUpdateItemName);
            name.setText(item.name);

            final EditText description = (EditText) view.findViewById(R.id.DialogUpdateItemDes);
            description.setText(item.description);


            ImageButton UpdateDataButton = (ImageButton)view.findViewById(R.id.DialogUpdateDataButton);
            if(item.user.equals(DataAccess.getUserId())) {
                UpdateDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DataAccess.myListItems.remove(item);

                        Location MyLocation =  SaveSettingsUser.getLocation();
                        Item itemTemp = new Item();
                        itemTemp.id=item.getId();
                        itemTemp.user=item.getUser();
                        itemTemp.setName(name.getText().toString());
                        itemTemp.setDescription(description.getText().toString());
                       // item.setImagesUri(launchCameraActivity.fileUri.get(0).getLastPathSegment().toString());
                        itemTemp.setImagesUri(item.getImagesUri());
                        itemTemp.setLatitude( MyLocation.getLatitude());
                        itemTemp.setLongitude( MyLocation.getLongitude());
                        itemTemp.setTimeAddItem(new Date().getTime()+"");

                        DataAccess.myListItems.add(itemTemp);
                        DataAccess.updateObject(itemTemp);
                        adapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
            }else{
                UpdateDataButton.setVisibility(View.GONE);
            }


            ImageButton BackButton = (ImageButton) view.findViewById(R.id.DialogGoBackButton);
            BackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
            });


        }

    }
    // get news from server

}
