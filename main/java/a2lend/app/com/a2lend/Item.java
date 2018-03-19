package a2lend.app.com.a2lend;

import android.location.Location;
import android.media.Image;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Igbar on 2/2/2018.
 */

public class Item {

    public  String id;

    public String name;
    public String description;
    public String user;


    public boolean stateToLend;
    public int daysToLend;

    public String imagesUri;
    public double latitude;
    public double longitude;
    public String timeAddItem;

    public Item() {

    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Item(String name, String description, String user, boolean stateToLend) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.stateToLend = stateToLend;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item)
              return ((Item)obj).id.equals(this.getId());
        return false;

    }

    //Getters Setters Default
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isStateToLend() {
        return stateToLend;
    }

    public void setStateToLend(boolean stateToLend) {
        this.stateToLend = stateToLend;
    }

    public int getDaysToLend() {
        return daysToLend;
    }

    public void setDaysToLend(int daysToLend) {
        this.daysToLend = daysToLend;
    }

    public String getImagesUri() {
        return imagesUri;
    }

    public void setImagesUri(String imagesUri) {
        this.imagesUri = imagesUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimeAddItem() {
        return timeAddItem;
    }

    public void setTimeAddItem(String timeAddItem) {
        this.timeAddItem = timeAddItem;
    }
}
