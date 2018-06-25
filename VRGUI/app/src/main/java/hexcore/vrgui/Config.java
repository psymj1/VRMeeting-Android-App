package hexcore.vrgui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Config extends Application{

    private static boolean shownWarning = false;

    public static boolean hasShownWarning()
    {
        return shownWarning;
    }

    public static void setShownWarning(boolean value)
    {
        shownWarning = value;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    /**
     *
     * @return the IP address to use for all the server calls
     */
    static public String getWebServerIdentifier(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_settings_key), Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.preference_settings_hostname_key),context.getString(R.string.preferences_settings_default_hostname));
    }

    static public int getWebServerPort(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_settings_key), Context.MODE_PRIVATE);
        return  sharedPref.getInt(context.getString(R.string.preference_settings_hostport_key),Integer.valueOf(context.getString(R.string.preferences_settings_default_hostport)));
    }

    static public String getWebServerURL(Context context)
    {
        return getWebServerIdentifier(context) + ":" + getWebServerPort(context);
    }
}
