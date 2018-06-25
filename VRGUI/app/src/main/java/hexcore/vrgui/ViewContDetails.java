package hexcore.vrgui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewContDetails extends AppCompatActivity {

    EditText txtFirstName;
    EditText txtSurname;
    EditText txtCompany;
    EditText txtJobTitle;
    EditText txtPhoneNum;
    EditText txtEmail;

    /**
     * Method used to initialise the activity instance
     * Retrieves the contact information to display
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cont_details);
        //retrieveContInfo();
    }

    /**
     * Gets the contact information from the first name and company name of the contact
     */

    public void retrieveContInfo() {
        //Sent contact first name and company through an intent, could be used to get rest of information?
        final ArrayList<String> contInfo = getIntent().getExtras().getStringArrayList("contInfo");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/contactInfo";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String contactFName;
                String contactCompany;
                String contactSurname;
                String contactJob;
                String contactPhone;
                String contactEmail;
                try {
                    contactFName = "First name: " + jsonObject.get("firstname");
                    contactCompany = "Surname: " + jsonObject.get("company");
                    contactSurname = "Company: " + jsonObject.get("surname");
                    contactJob = "Job title: " + jsonObject.get("jobtitle");
                    contactPhone = "Phone number: " + jsonObject.get("phonenum");
                    contactEmail = "Email address: " + jsonObject.get("email");
                    txtFirstName.setText(contactFName);
                    txtSurname.setText(contactSurname);
                    txtCompany.setText(contactCompany);
                    txtJobTitle.setText(contactJob);
                    txtPhoneNum.setText(contactPhone);
                    txtEmail.setText(contactEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SERVER RESPONSE:", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtFirstName.requestFocus();
                txtFirstName.setError("Cannot find contact information");
            }
        }) ;
        queue.add(stringRequest);
    }
}
