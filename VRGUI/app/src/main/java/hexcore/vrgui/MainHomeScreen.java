package hexcore.vrgui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainHomeScreen extends AppCompatActivity {

    TextView lblHeader;
    EditText txtCode;
    String uToken;
    int userID;

    /**
     * Method used to initialise the activity instance
     * It reads the file in the internal storage containing the token if it exists and displays a 'Welcome back' message to the user
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_screen);

        lblHeader = findViewById(R.id.lblMain);
        txtCode = findViewById(R.id.txtMeetingCode);

        uToken = ReadToken.readFromFile("token.txt", this);
        setUserInfo(uToken);
    }

    /**
     * Function to disable the phone's back button
     */

    @Override
    public void onBackPressed() {
    }

    /**
     * GET request to get the user's name to display a 'Welcome back' message
     * @param token user's token to indentify the user
     */

    public void setUserInfo(final String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/user/token";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String name = null;
                try {
                    name = (String) jsonObject.get("firstname");
                    userID = (int) jsonObject.get("userid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lblHeader.setText("Welcome back, " + name);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token); //send saved token to server
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Loads an activity which allows Unity to launch
     * Validation used to check if the meeting txtCode is valid
     * @param view
     */

    public void onPressJoin(View view) {
        String mCode = txtCode.getText().toString();
        if (mCode.length() == 0) {
            txtCode.requestFocus();
            txtCode.setError("Field cannot be empty");
        }
        else
        {
            joinMeetingRequest(mCode, uToken);
        }
    }

    /**
     * Loads the create meeting activity
     * @param view
     */

    public void onPressCreate(View view) {
        startActivity(new Intent(MainHomeScreen.this, CreateMeeting.class));
    }

    /**
     * Loads the saved contacts activity
     * @param view
     */
    public void onPressSavedContacts(View view) {
        startActivity(new Intent(MainHomeScreen.this, SavedContacts.class));
    }

    /**
     * Logs the user out of the app
     * Deletes any trace of the user's details off the device when logging out
     * @param view
     */

    public void onPressLogout(View view) {
        File dir = getFilesDir();
        File tokenFile = new File(dir, "token.txt");
        File userFile = new File(dir, "user.txt");
        boolean deleted = tokenFile.delete();
        boolean deleted2 = userFile.delete();
        startActivity(new Intent(MainHomeScreen.this, LoginScreen.class));
    }

    /**
     * Makes a request to the server to check if the txtCode a user enters is valid
     * If valid, start unity system
     * If not valid, return not valid
     * @param meetingCode is the txtCode that the user enters to join the meeting
     * @param token is the unique txtCode that represents a user
     */

    public void joinMeetingRequest(final String meetingCode, final String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/joinmeeting";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String hIP = (String) jsonObject.get("hIP");
                    int hPort = Integer.parseInt((String) jsonObject.get("hPort"));
                    String fIP = (String)jsonObject.get("fIP");
                    int fPort = Integer.parseInt((String)jsonObject.get("fPort"));
                    startActivity(generateUnityIntent(token,meetingCode,hIP,hPort,fIP,fPort));   //dont delete, just for testing unity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtCode.requestFocus();
                txtCode.setError("Something went wrong, please check your internet connection");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token); //send saved token to server
                return headers;
            }
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("mCode", meetingCode);
                return data;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Generates the necessary intent to launch the unity application from the Android App
     * @param token The Authentication token of the user
     * @param meetingCode The code for the meeting the user is attempting to join
     * @return An intent that can be passed to an activity to launch the unity app with the required parameters
     */
    private Intent generateUnityIntent(String token, String meetingCode,String hostServerIP,int hostServerPort,String fileServerIP,int fileServerPort)
    {
        Intent intent = new Intent(MainHomeScreen.this,com.Hexcore.VRMeeting.UnityPlayerActivity.class);

        Bundle params = new Bundle();
        params.putString("AuthToken",token);
        params.putString("MeetingCode",meetingCode);
        params.putString("WebServerIdentifier",Config.getWebServerIdentifier(this));
        params.putString("HostServerIdentifier",hostServerIP);
        params.putString("FileServerIdentifier",fileServerIP);
        params.putInt("WebServerPort",Config.getWebServerPort(this));
        params.putInt("HostServerPort",hostServerPort);
        params.putInt("FileServerPort",fileServerPort);
        params.putInt("UserID",userID);
        intent.putExtras(params);
        return intent;
    }
}