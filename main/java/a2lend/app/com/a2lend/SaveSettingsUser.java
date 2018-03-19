package a2lend.app.com.a2lend;

import android.location.Location;

/**
 * Created by Igbar on 2/10/2018.
 */

public class SaveSettingsUser {

    public static Location location;





    public static Location getLocation() {
        return location;
    }

    public static void setLocation(Location location) {
        SaveSettingsUser.location = location;
    }


}
