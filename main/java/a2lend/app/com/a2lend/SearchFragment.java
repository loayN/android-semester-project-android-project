package a2lend.app.com.a2lend;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Igbar on 1/23/2018.
 */

public class SearchFragment extends Fragment {

    boolean flagRefreshList = true;
    MyCustomAdapter adapter;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,null);
        //super.onCreateView(inflater, container, savedInstanceState);
        // on create
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  created

        if(SaveSettingsUser.getLocation() == null){
            MySupport.showMessageDialog(getContext(),"Your location is not known","To be used in the function should be declared your location");
            MySupport.goToFragment(new ProfileUpdateFragment(),getActivity());
        }



        //region init ListView
        listView = (ListView) view.findViewById(R.id.SearchListView);
        adapter = new MyCustomAdapter(DataAccess.resulSearchList); // MyListItems
        listView.setAdapter(adapter);
        //endregion

        final  TextView searchName = (TextView) getActivity().findViewById(R.id.SearchName);

        //region SearchButtons
        ImageButton SearchButtonUp = (ImageButton)getActivity().findViewById(R.id.search_SearchButtonUp);
        ImageButton SerachButtonDown = (ImageButton)getActivity().findViewById(R.id.search_SerachButtonDown);

        View.OnClickListener onClickListenerSearch = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ItemName = searchName.getText().toString();
                listView.clearAnimation();
                DataAccess.SearchByName(ItemName);
                if(DataAccess.resulSearchList.size()==0) {
                    Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_SHORT).show();
                }
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                adapter.notifyDataSetChanged();

            }
        };
        SearchButtonUp.setOnClickListener(onClickListenerSearch);
        SerachButtonDown.setOnClickListener(onClickListenerSearch);
        //endregion

        //region Go SearchLocationButton
        ImageButton SearchLocationButton = (ImageButton)getActivity().findViewById(R.id.search_SearchButtunLocation);
        SearchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ItemName = searchName.getText().toString();

                listView.clearAnimation();
                // Search
                DataAccess.SearchByName(ItemName);

                if(DataAccess.resulSearchList.size()==0) {
                    Snackbar.make(v, "Result Not Found ! ", Snackbar.LENGTH_LONG)
                            .setAction("Search", null).show();
                }
                //region hide Keyboard
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                //endregion

                adapter.notifyDataSetChanged();
                MySupport.goToFragment(new SearchByLocation(),getActivity());
            }
        });
        //endregion

        //region Go FragmentHome
        ImageButton GoBackButton = (ImageButton)getActivity().findViewById(R.id.Home);
        GoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //region hide Keyboard
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                //endregion
                MySupport.goToFragment(new ControlPanelFragment(),getActivity());
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
                    flagRefreshList = false;
                    adapter.notifyDataSetChanged();
                }
            }
        }, 100);
        //endregion Handler flagRefreshList
    }

   //region Adapter
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

            View myView = mInflater.inflate(R.layout.search_tool_item, null);

            TextView name = (TextView) myView.findViewById(R.id.SearchViewItemName);
            name.setText(item.name);

            TextView des = (TextView) myView.findViewById(R.id.SearchViewItemDes);
            des.setText(item.description);

            final ImageView imageView =(ImageView) myView.findViewById(R.id.SearchImageViewItem);
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
                    ShowItemDialog(item);
                }
            });

            //region ShowButton
            ImageButton ShowButton = (ImageButton)myView.findViewById(R.id.searchitem_show);
            ShowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowItemDialog(item);
                }
            });
            //endregion

            //region searchitem_call
            ImageButton CallButton = (ImageButton)myView.findViewById(R.id.searchitem_call);
            CallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    adapter.notifyDataSetChanged();
                    flagRefreshList=true;

                }
            });
            //endregion

            //region LocationItem
            ImageButton LocationItem = (ImageButton)myView.findViewById(R.id.searchItem_locationButton);
            LocationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //endregion

            return myView;
        }

        private void ShowItemDialog(Item item){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(item.name);
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View view = factory.inflate(R.layout.show_item, null);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog  = alertDialogBuilder.show();
            ImageView image= (ImageView) view.findViewById(R.id.ImageViewFullScreen);

            StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("photos").child(item.getImagesUri());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(image);


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

        private void UpdateItemDialog(final Item item){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Update");
            LayoutInflater factory = LayoutInflater.from(getContext());
            View view = factory.inflate(R.layout.update_item, null);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog  = alertDialogBuilder.show();

            ImageView image= (ImageView) view.findViewById(R.id.DialogUpdateItemImage);

            StorageReference mStorageRef;
            mStorageRef = FirebaseStorage.getInstance().getReference().child("photos").child(item.getImagesUri());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(image);

            final EditText name = (EditText) view.findViewById(R.id.DialogUpdateItemName);
            name.setText(item.name);

            final EditText description = (EditText) view.findViewById(R.id.DialogUpdateItemDes);
            description.setText(item.description);

            //region UpdateDataButton
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

        //endregion

            ImageButton BackButton = (ImageButton) view.findViewById(R.id.DialogGoBackButton);
            BackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });


        }

    }
    //endregion


}
