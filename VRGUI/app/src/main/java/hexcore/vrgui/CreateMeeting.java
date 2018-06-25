package hexcore.vrgui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class CreateMeeting extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    Button filePick;
    EditText txtMName;
    EditText txtMDesc;
    private String selectedFilePath;
    private String mCode;

    /**
     * Method used to initialise the activity instance
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        filePick = findViewById(R.id.btnUploadFile);
        txtMName = findViewById(R.id.txtMeetingName);
        txtMDesc = findViewById(R.id.txtMeetingDesc);
    }

    /**
     * Uploads the user's presentation files
     * Sets the activity to only allow for PDFs
     * Allows to select data and return it
     * Starts new activity to select file and return data
     * @param v
     */

    public void onPressUpload(View v) {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    /**
     * Ensures selected file is valid
     * Sets the button text to the name of the file selected
     * Converts the filepath to a valid local storage type path
     * @param requestCode
     * @param resultCode
     * @param data information returned from picking file
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) return;
                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    String filename = selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1);
                    filePick.setText(filename);
                } else {
                    Toast.makeText(this, "Can only upload local device files, try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Retrieves the data entered by the user and stores it as strings to then be passed to the server call to create the meeting
     * data includes reading the user's unique token
     * @param view
     */

    public void onPressFinish(View view) {
        final String mName = txtMName.getText().toString();
        final String mDesc = txtMDesc.getText().toString();
        String uToken = ReadToken.readFromFile("token.txt", this);
        String webServerURL = Config.getWebServerURL(this);
        createMeetingRequest(mName, mDesc, uToken,webServerURL);
    }

    /**
     * POST request to server to create meeting with authentication lblHeader
     * Starts file upload thread if file was selected passing the unique meeting txtCode from initial server request response
     * Copies the server response txtCode to the clipboard used for sharing with people
     * @param mName meeting name to be sent to the server
     * @param mDesc meeting description to be sent to the server
     * @param uToken authentication token used to indentify the user
     */

    public void createMeetingRequest(final String mName, final String mDesc, final String uToken,final String webServerURL) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/createmeeting";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mCode = null;
                try {
                    mCode = (String) jsonObject.get("meeting_code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            new UploadFileAsync().execute(selectedFilePath, mCode, uToken,webServerURL);
                        }
                    }).start();
                }

                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String copyText = mName + ". " + mDesc + ". Join now using code '" + mCode + "' via VR Meeting Android app developed by Hexcore. Download the app here here: " + Config.getWebServerURL(CreateMeeting.this) + "/download/apk";
                ClipData myClip;
                myClip = ClipData.newPlainText("Meeting Code", copyText);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Meeting Code, " + mCode + " has been saved to clipboard, share it now", Toast.LENGTH_LONG).show();

                startActivity(new Intent(CreateMeeting.this, MainHomeScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtMName.requestFocus();
                txtMName.setError("Something went wrong, please check your internet connection");
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", uToken); //send saved token to server
                return headers;
            }

            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("MeetingName", mName);
                data.put("MeetingDescription", mDesc);
                data.put("DoM", "01/05/2018");
                data.put("ToM", "00:00");
                return data;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Goes back to the main home screen
     * @param view
     */

    public void onPressCancel(View view) {
        startActivity(new Intent(CreateMeeting.this, MainHomeScreen.class));
    }

    /**
     * Function to disable the phone's back button
     */

    @Override
    public void onBackPressed() {
    }


}
