package hexcore.vrgui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ForgotPassword extends AppCompatActivity {

    EditText txtValidEmail;
    TextView txtQues1;
    TextView txtQues2;
    TextView txtQues3;
    EditText txtNewPass1;
    EditText txtNewPass2;
    EditText txtAnswer1;
    EditText txtAnswer2;
    EditText txtAnswer3;
    Button finishButton;
    String userEmail;

    /**
     * Method used to initialise the activity instance
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        txtValidEmail = findViewById(R.id.txtValidEmail);
        txtQues1 = findViewById(R.id.lblQues1);
        txtQues2 = findViewById(R.id.lblQues2);
        txtQues3 = findViewById(R.id.lblQues3);
        txtNewPass1 = findViewById(R.id.txtNewPass1);
        txtNewPass2 =  findViewById(R.id.txtNewPass2);
        txtAnswer1 = findViewById(R.id.txtAnswer1);
        txtAnswer2 = findViewById(R.id.txtAnswer2);
        txtAnswer3 = findViewById(R.id.txtAnswer3);
        finishButton = findViewById(R.id.btnComplete);
    }

    /**
     * Function to disable the phone's back button
     */

    @Override
    public void onBackPressed() {
    }

    /**
     * Goes back to the login screen
     * @param view
     */

    public void cancelReset(View view) {
        startActivity(new Intent(ForgotPassword.this, LoginScreen.class));
    }

    /**
     * Validation checks to check the field is not left empty and is valid
     * The keyboard is hidden once a valid email address is entered
     * @param view
     */

    public void onPressValidate(View view) {
        final String validEmail = txtValidEmail.getText().toString();
        if (validEmail.length() == 0) {
            txtValidEmail.requestFocus();
            txtValidEmail.setError("Please enter an email address");
        }
        else if (!validEmail.matches("^(.+)@(.+)$")) {
            txtValidEmail.requestFocus();
            txtValidEmail.setError("Invalid email address");
        }
        else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            authenticateUserEmail(validEmail);
        }
    }

    /**
     * GET request to server with the authenticate request to check if the email address exists
     * If the user is not found - display error message to user, otherwise retrieve the security questions from the server
     * @param email email address to check entered by the user
     */

    public void authenticateUserEmail(final String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/exists?email="+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                retrieveSecurityQuestions(email);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtValidEmail.requestFocus();
                txtValidEmail.setError("Email address not found, please try again");
            }
        }) ;
        queue.add(stringRequest);
    }

    /**
     * GET request to server with the authenticate request to retrieve the security questions for that user
     * The questions and answer boxes then appear on the screen for the user to complete and reset their password
     * An error is displayed if the security questions cannot be found
     * @param email email address to get security questions from
     */

    public void retrieveSecurityQuestions(final String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/securityquestions?email="+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String ques1 = null;
                String ques2 = null;
                String ques3 = null;
                try {
                    ques1 = (String) jsonObject.get("question1");
                    ques2 = (String) jsonObject.get("question2");
                    ques3 = (String) jsonObject.get("question3");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtQues1.setVisibility(View.VISIBLE);
                txtQues2.setVisibility(View.VISIBLE);
                txtQues3.setVisibility(View.VISIBLE);
                txtAnswer1.setVisibility(View.VISIBLE);
                txtAnswer2.setVisibility(View.VISIBLE);
                txtAnswer3.setVisibility(View.VISIBLE);
                txtNewPass1.setVisibility(View.VISIBLE);
                txtNewPass2.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.VISIBLE);
                txtQues1.setText(ques1);
                txtQues2.setText(ques2);
                txtQues3.setText(ques3);
                userEmail = email;
                Log.d("SERVER RESPONSE:", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtValidEmail.requestFocus();
                txtValidEmail.setError("Error: Security Questions Not Found");
            }
        }) ;
        queue.add(stringRequest);
    }

    /**
     * Retrieves all the answers to the questions entered by the user and stores it as strings
     * Several validation checks are used - first it checks that all mandatory fields are completed
     * Makes sure that both password fields entered are equal and that the password contains 8 characters with at least 1 number
     * The user's answers are then sent to the server to be checked
     * @param view
     */

    public void onPressFinish(View view) {
        final String newPass1 = txtNewPass1.getText().toString();
        final String newPass2 = txtNewPass2.getText().toString();
        final String answer1 =  txtAnswer1.getText().toString();
        final String answer2 = txtAnswer2.getText().toString();
        final String answer3 = txtAnswer3.getText().toString();
        if (answer1.length() == 0) {
            txtAnswer1.requestFocus();
            txtAnswer1.setError("Field cannot be empty");
        } else if (answer2.length() == 0) {
            txtAnswer2.requestFocus();
            txtAnswer2.setError("Field cannot be empty");
        } else if (answer3.length() == 0) {//Makes sure both password fields entered are equal
            txtAnswer3.requestFocus();
            txtAnswer3.setError("Field cannot be empty");
        }
        if (newPass1.length() == 0) {
            txtNewPass1.requestFocus();
            txtNewPass1.setError("Field cannot be empty");
        } else if (newPass2.length() == 0) {
            txtNewPass2.requestFocus();
            txtNewPass2.setError("Field cannot be empty");
        } else if (!newPass2.equals(newPass1)) {//Makes sure both password fields entered are equal
            txtNewPass2.requestFocus();
            txtNewPass2.setError("Passwords must match");
        }
        else if (!newPass1.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$")) //Ensures the password contains 8 characters and at least 1 number
        {
            txtNewPass1.requestFocus();
            txtNewPass1.setError("Invalid password, must contain 8 characters and at least 1 number");
        }
        else{
            validateAnswers(answer1, answer2, answer3, newPass1, newPass2);
        }
    }

    /**
     * POST request to server with the authenticate request - checks that the answers entered matches those on the server
     * If answers do not match the server records an error message is displayed
     * @param ans1 answer to question 1
     * @param ans2 answer to question 2
     * @param ans3 answer to question 3
     * @param newpass1 new password
     * @param newpass2 new password check
     */

    public void validateAnswers(final String ans1, final String ans2, final String ans3, final String newpass1, final String newpass2) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/validateanswers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                startActivity(new Intent(ForgotPassword.this, LoginScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtNewPass1.requestFocus();
                txtNewPass1.setError("Answers do not match our records, try again");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("email", userEmail);
                data.put("answer1", ans1);
                data.put("answer2", ans2);
                data.put("answer3", ans3);
                data.put("newpassword", newpass1);
                data.put("passwordcheck", newpass2);
                return data;
            }
        };
        queue.add(stringRequest);
    }
}