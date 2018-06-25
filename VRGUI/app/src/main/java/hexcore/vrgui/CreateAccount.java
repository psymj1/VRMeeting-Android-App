package hexcore.vrgui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class CreateAccount extends AppCompatActivity {

    EditText txtEmailCre;
    EditText txtPassCre;
    EditText txtPassCre2;
    EditText txtFirstCre;
    EditText txtSurnameCre;
    EditText txtCompanyCre;
    EditText txtJobCre;
    EditText txtWorkPhoneCre;
    EditText txtWorkEmailCre;
    Spinner spinGender;

    /**
     * Method used to initialise the activity instance
     * The Spinner used for gender selection is also initialised below
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        txtEmailCre = findViewById(R.id.txtEmailCheck);
        txtPassCre = findViewById(R.id.txtPassCre);
        txtPassCre2 = findViewById(R.id.txtPassCre2);
        txtFirstCre = findViewById(R.id.txtFirstCre);
        txtSurnameCre = findViewById(R.id.txtSurnameCre);
        txtCompanyCre = findViewById(R.id.txtCompanyCre);
        txtJobCre = findViewById(R.id.txtJobCre);
        txtWorkPhoneCre = findViewById(R.id.txtWorkPhoneCre);
        txtWorkEmailCre = findViewById(R.id.txtWorkEmailCre);
        spinGender = findViewById(R.id.spinGender);

        String[] arraySpinner = new String[] {
                "Male","Female"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(adapter);
    }

    /**
     * Function retrieves all the data entered by the user and stores it as strings
     * Several validation checks are used - first it checks that all mandatory fields are completed
     * Makes sure that both password fields entered are equal
     * Regular expressions are used to check the email entered is valid and ensures that the password contains 8 characters with at least 1 number
     * The user information is then stored in an Array List and passed to the next page where it can be sent to the server
     * @param view
     */

    public void onPressCreateAc(View view) {
        System.out.println("Testing");
        final String emailCre = txtEmailCre.getText().toString();
        final String passwordCre = txtPassCre.getText().toString();
        final String passwordCre2 = txtPassCre2.getText().toString();
        final String firstNameCre = txtFirstCre.getText().toString();
        final String surnameCre = txtSurnameCre.getText().toString();
        final String companyCre = txtCompanyCre.getText().toString();
        final String jobTitleCre = txtJobCre.getText().toString();
        final String phoneNumCre = txtWorkPhoneCre.getText().toString();
        final String workEmailCre = txtWorkEmailCre.getText().toString();
        final String gender = spinGender.getSelectedItem().toString();

        if (emailCre.length() == 0) {
            txtEmailCre.requestFocus();
            txtEmailCre.setError("Field cannot be empty");
        } else if (passwordCre.length() == 0) {
            txtPassCre.requestFocus();
            txtPassCre.setError("Field cannot be empty");
        } else if (passwordCre2.length() == 0) {
            txtPassCre2.requestFocus();
            txtPassCre2.setError("Field cannot be empty");
        } else if (!passwordCre2.equals(passwordCre)) {
            txtPassCre2.requestFocus();
            txtPassCre2.setError("Passwords must match");
        } else if (firstNameCre.length() == 0) {
            txtFirstCre.requestFocus();
            txtFirstCre.setError("Field cannot be empty");
        } else if (surnameCre.length() == 0) {
            txtSurnameCre.requestFocus();
            txtSurnameCre.setError("Field cannot be empty");
        } else if (companyCre.length() == 0) {
            txtCompanyCre.requestFocus();
            txtCompanyCre.setError("Field cannot be empty");
        } else if (jobTitleCre.length() == 0) {
            txtJobCre.requestFocus();
            txtJobCre.setError("Field cannot be empty");
        } else if (phoneNumCre.length() != 0) {
            if (phoneNumCre.length() != 11)
            {
                txtWorkPhoneCre.requestFocus();
                txtWorkPhoneCre.setError("Field cannot be empty and must be 11 characters");
            } else if (phoneNumCre.charAt(0) != '0')
            {
                txtWorkPhoneCre.requestFocus();
                txtWorkPhoneCre.setError("Invalid phone number entered");
            }
        } else if (!emailCre.matches("^(.+)@(.+)$"))
        {
            txtEmailCre.requestFocus();
            txtEmailCre.setError("Invalid email address");
        } else if (!passwordCre.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$"))
        {
            txtPassCre.requestFocus();
            txtPassCre.setError("Invalid password, must contain 8 characters and at least 1 number");
        } else if (workEmailCre.length() != 0) {
            if ((!emailCre.matches("^(.+)@(.+)$"))) {
                txtWorkEmailCre.requestFocus();
                txtWorkEmailCre.setError("Invalid email address");
            }
        } else {
            ArrayList<String> userInfo = new ArrayList<String>();
            userInfo.add(emailCre);
            userInfo.add(passwordCre);
            userInfo.add(passwordCre2);
            userInfo.add(firstNameCre);
            userInfo.add(surnameCre);
            userInfo.add(companyCre);
            userInfo.add(jobTitleCre);
            userInfo.add(phoneNumCre);
            userInfo.add(workEmailCre);
            userInfo.add(gender);
            authenticateUserEmail(emailCre, userInfo);
        }
    }

    /**
     * GET request to the server to check the email address hasn't already been used to create an account
     * If the email address already exists an error message is shown, if not, the Avatar Selection page is loaded and displayed
     * @param email the email address to check
     * @param userInfo the Array List containing all the user's details
     */

    public void authenticateUserEmail(final String email, final ArrayList userInfo) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/exists?email=" + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtEmailCre.requestFocus();
                txtEmailCre.setError("Email address already used, please try again");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(CreateAccount.this, AvatarSelection.class);
                intent.putStringArrayListExtra("userInfo", userInfo);
                startActivity(intent);
            }
        });
        queue.add(stringRequest);
    }
}