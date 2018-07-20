package garmin.com.academyshop.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import garmin.com.academyshop.R;

import static garmin.com.academyshop.AcademyShopApplication.APPLICATION_CONTEXT;

/**
 * Created by Octavian on 5/8/2017.
 */

public class NetworkUtils {

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info!=null && info.getDetailedState() ==NetworkInfo.DetailedState.CONNECTED){
            return true;
        }
        return false;
    }

    public static boolean isConnectedToWifiNetwork(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info!=null && info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED
                && info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    public static boolean idWifiOnlyNetworkPreferenceSet(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean
                (context.getString(R.string.key_wifi_only),context.getResources().getBoolean(R.bool.wifi_only_default));
    }



}
