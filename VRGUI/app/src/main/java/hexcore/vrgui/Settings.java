package hexcore.vrgui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    EditText hostName;
    EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hostName = findViewById(R.id.txtHostName);
        port = findViewById(R.id.txtPortNumber);
        Log.i("Settings",hostName.getText().toString());
        Log.i("Settings",port.getText().toString());
        LoadSettings(this);
    }

    public void onPressBack(View view)
    {
        startActivity(new Intent(Settings.this, LoginScreen.class));
    }

    public void onPressApply(View view)
    {
        SaveSettings(this);
    }

    private void LoadSettings(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_settings_key), Context.MODE_PRIVATE);

        String hName = sharedPref.getString(getString(R.string.preference_settings_hostname_key),context.getString(R.string.preferences_settings_default_hostname));
        int hPort = sharedPref.getInt(getString(R.string.preference_settings_hostport_key),Integer.valueOf(context.getString(R.string.preferences_settings_default_hostport)));

        hostName.setText(hName);
        port.setText(String.valueOf(hPort));
    }

    private void SaveSettings(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_settings_key), Context.MODE_PRIVATE);

        try
        {
            String hName = hostName.getText().toString();
            if(hName.isEmpty())
            {
                throw new IllegalArgumentException("The host name/IP cannot be empty");
            }
            int hPort = Integer.valueOf(port.getText().toString());
            if(hPort < 0 || hPort > 65535)
            {
                throw new IllegalArgumentException("The port must be a number between 0 - 65535");
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.preference_settings_hostname_key),hName);
            editor.putInt(getString(R.string.preference_settings_hostport_key),hPort);
            editor.apply();
            startActivity(new Intent(Settings.this, LoginScreen.class));
        }catch(NumberFormatException e)
        {
            port.setError("The port must be a number between 0 - 65535");
        }catch(IllegalArgumentException i)
        {
            if(i.getMessage().contains("name"))
            {
                hostName.setError(i.getMessage());
            }else
            {
                port.setError(i.getMessage());
            }
        }

    }

}
