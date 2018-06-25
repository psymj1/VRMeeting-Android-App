package hexcore.vrgui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    EditText txtEmailCre;
    EditText txtPassCre;
    Switch swRemember; //remember me toggle
    private static final int PERMISSION_REQUEST_CODE = 1;

    /**
     * Method used to initialise the activity instance
     * Requests permission for the app to use internal storage
     * Reads the internal storage to see if there is a token already stored on device
     * If there is, it passes the token to server to see if it's valid to auto log-in the user
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Config.hasShownWarning())
        {
            ConfirmAppInDevleopmentDialogFragment dialog = new ConfirmAppInDevleopmentDialogFragment();
            dialog.show(getSupportFragmentManager(),"ConfirmAppInDevelopmentDialogFragment");
            Config.setShownWarning(true);
        }

        setContentView(R.layout.activity_login);

        if (!checkPermission()){
            requestPermission();
        }

        txtEmailCre = findViewById(R.id.txtEmailCheck);
        txtPassCre = findViewById(R.id.txtPassCre);
        String loginToken = ReadToken.readFromFile("token.txt", this);
        if (loginToken != "") {
            authenticateToken(loginToken);
        }
        //Underlines the 'Forgot Password' text
        TextView textView = findViewById(R.id.lblForgotPass);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * Function to disable the phone's back button
     */

    @Override
    public void onBackPressed() {
    }

    /**
     * Checks user's permission
     * @return boolean indicating if the user has agreed to using their internal storage
     */

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
         else
            return false;

    }

    /**
     * Pop-up is shown to the user requesting permission to access internal storage
     */

    private void requestPermission() {
        ArrayList<String> permissionsRequired = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, Manifest.permission.RECORD_AUDIO))
                Toast.makeText(LoginScreen.this, "Record Audio allows us to capture and stream your voice when presenting to your viewers. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            else
                permissionsRequired.add(Manifest.permission.RECORD_AUDIO);
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                Toast.makeText(LoginScreen.this, "Write External Storage permission allows us to store meta data. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            else
                permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, Manifest.permission.MODIFY_AUDIO_SETTINGS))
                Toast.makeText(LoginScreen.this, "Modify Audio Settings allows us to stream your voice when presenting to your viewers. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            else
                permissionsRequired.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }

        if(permissionsRequired.size() > 0)
        {
            ActivityCompat.requestPermissions(LoginScreen.this, permissionsRequired.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Displays the create account activity
     * @param view
     */

    public void onPressCreate(View view) {
        startActivity(new Intent(LoginScreen.this, CreateAccount.class));
    }

    /**
     * Displays the forgot password activity
     * @param view
     */

    public void onForgotPassword(View view) {
        startActivity(new Intent(LoginScreen.this, ForgotPassword.class));
    }

    /**
     * Validation checks used to make sure the fields are not left empty and the email and password is valid
     * When login is pressed it authenticates the user with the server
     * @param view
     */

    public void onPressLogin(View view) {
        final String emailCre = txtEmailCre.getText().toString();
        final String passwordCre = txtPassCre.getText().toString();
        if (emailCre.length() == 0) {
            txtEmailCre.requestFocus();
            txtEmailCre.setError("Field cannot be empty");
        } else if (passwordCre.length() == 0) {
            txtPassCre.requestFocus();
            txtPassCre.setError("Field cannot be empty");
        } else if (!emailCre.matches("^(.+)@(.+)$")) {
            txtEmailCre.requestFocus();
            txtEmailCre.setError("Invalid email address");
        } else if (!passwordCre.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$")) {
            txtPassCre.requestFocus();
            txtPassCre.setError("Invalid password, must contain 8 characters and at least 1 number");
        } else {
            authenticateUserRequest(emailCre, passwordCre);
        }
    }

    /**
     * Function for authenticating a user - a POST request is sent to the server to check if the credentials match the server record
     * Checks if the remember me toggle has been switched so that the token that is returned from the server call is saved to the device
     * The user's the credentials are sent to the server as a hashmap
     * If the user is not found - display error message to user, otherwise display the main home screen
     * @param email email address to check
     * @param password password to check
     */

    public void authenticateUserRequest(final String email, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/authenticate";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swRemember = findViewById(R.id.swRemember);
                if (swRemember.isChecked()) {
                    String credentials = email + ":" + password;
                    saveToFile(credentials, "user.txt");
                }
                saveToFile(response, "token.txt");
                startActivity(new Intent(LoginScreen.this, MainHomeScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtPassCre.requestFocus();
                txtPassCre.setError("User not found, please try again");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * The passed data and filename are used to save a file on the device - used for token and user storage
     * @param data data to be stored in the file
     * @param file filename
     */

    public void saveToFile(String data, String file) {

        if (file.equals("token.txt"))
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                data = (String) jsonObject.get("auth_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Function that checks the token on the internal storage to auto login the user if valid
     * If token is valid then login
     * If token has expired then check if the user has saved credentials previously to then contact server and get a new token automatically
     * If user hasn't saved credentials then they will re-login
     * @param token user's token to check
     */

    public void authenticateToken(final String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/isvalid";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                startActivity(new Intent(LoginScreen.this, MainHomeScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String savedDetails = readFromFile("user.txt");
                if (savedDetails.equals("")) {
                    authenticateUserRequest("auth_token", savedDetails);
                } else {
                    txtEmailCre.requestFocus();
                    txtEmailCre.setError("Session Expired, please re-login");
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Reads the file stored in internal storage and retrieves the information for auto login
     * @param file filename
     * @return the details of the user so they can be auto logged-in
     */

     private String readFromFile(String file) {
        String details = "";
        try {
            InputStream inputStream = this.openFileInput(file);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                details = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return details;
    }

    public void onPressNavigateSettings(View view)
    {
        startActivity(new Intent(LoginScreen.this, Settings.class));
    }

}