package hexcore.vrgui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SecurityQuestions extends AppCompatActivity {

    EditText txtAns1;
    EditText txtAns2;
    EditText txtAns3;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    Button buttonComplete;

    /**
     * Method used to initialise the activity instance
     * Sets each question displayed in the spinner
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);
        txtAns1 = findViewById(R.id.txtAns1);
        txtAns2 = findViewById(R.id.txtAns2);
        txtAns3 = findViewById(R.id.txtAns3);
        spinner1 = findViewById(R.id.spin1);
        spinner2 = findViewById(R.id.spin2);
        spinner3 = findViewById(R.id.spin3);
        buttonComplete = findViewById(R.id.btnComplete);
        try {
            setSecurityQuestions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the login activity
     * @param view
     */

    public void onPressCancel(View view)
    {
        startActivity(new Intent(SecurityQuestions.this, LoginScreen.class));
    }

    /**
     * Validation checks are used to ensure that each field cannot be left empty
     * Passes the questions and answers into a server call
     * @param view
     */

    public void onPressFinish(View view) {
        final String answer1 = txtAns1.getText().toString();
        final String answer2 = txtAns2.getText().toString();
        final String answer3 = txtAns3.getText().toString();
        final String question1 = spinner1.getSelectedItem().toString();
        final String question2 = spinner2.getSelectedItem().toString();
        final String question3 = spinner3.getSelectedItem().toString();
        if (answer1.length() == 0) {
            txtAns1.requestFocus();
            txtAns1.setError("Field cannot be empty");
        } else if (answer2.length() == 0) {
            txtAns2.requestFocus();
            txtAns2.setError("Field cannot be empty");
        } else if (answer3.length() == 0) {
            txtAns3.requestFocus();
            txtAns3.setError("Field cannot be empty");
        } else if (question1.equals(question2) || question1.equals(question3)) {
            txtAns1.requestFocus();
            txtAns1.setError("Cannot select the same question");
        } else if (question2.equals(question1) || question2.equals(question3)) {
            txtAns2.requestFocus();
            txtAns2.setError("Cannot select the same question");
        } else if (question3.equals(question2) || question3.equals(question1)) {
            txtAns3.requestFocus();
            txtAns3.setError("Cannot select the same question");
        } else {
            HideFinishButton();
            authenticateCreateRequest(question1,question2,question3,answer1,answer2,answer3);
        }
    }

    /**
     * Sets the spinner security questions specified in the saved file "securityQuestions.txt"
     * @throws IOException
     */

    public void setSecurityQuestions() throws IOException {
        Vector<String> string = new Vector<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("securityQuestions.txt")));
        String line = in.readLine();
        while (line != null) {
            string.add(line);
            line = in.readLine();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, string);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);
        spinner1.setSelection(1);
        spinner2.setSelection(2);
        spinner3.setSelection(3);
    }

    /**
     * POST request is sent to the server to make sure the user can create an account
     * Sends this data to the server to be stored on the database as well as the information saved in the previous Array List
     * @param question1 question 1 to be stored
     * @param question2 question 2 to be stored
     * @param question3 question 3 to be stored
     * @param answer1 answer 1 to be stored
     * @param answer2 answer 2 to be stored
     * @param answer3 answer 3 to be stored
     */

    public void authenticateCreateRequest(final String question1, final String question2, final String question3, final String answer1, final String answer2, final String answer3) {
        final ArrayList<String> userInfo = getIntent().getExtras().getStringArrayList("userInfo");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/register";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                startActivity(new Intent(SecurityQuestions.this, LoginScreen.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ShowFinishButton();
                txtAns3.requestFocus();
                txtAns3.setError("Something went wrong, please check your internet connection");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                assert userInfo != null;
                data.put("Email", userInfo.get(0));
                data.put("password", userInfo.get(1));
                data.put("passwordcheck", userInfo.get(2));
                data.put("FirstName", userInfo.get(3));
                data.put("SurName", userInfo.get(4));
                data.put("Company", userInfo.get(5));
                data.put("JobTitle", userInfo.get(6));
                if (userInfo.get(8).length()!= 0)
                {
                    data.put("WorkEmail", userInfo.get(8));
                }
                if (userInfo.get(7).length()!= 0)
                {
                    data.put("PhoneNum", userInfo.get(7));
                }
                data.put("Question1", question1);
                data.put("Answer1", answer1);
                data.put("Question2", question2);
                data.put("Answer2", answer2);
                data.put("Question3", question3);
                data.put("Answer3", answer3);
                data.put("AvatarID",userInfo.get(10));
                return data;
            }
        };
        queue.add(stringRequest);
    }

    public void HideFinishButton()
    {
        buttonComplete.setVisibility(View.INVISIBLE);
        buttonComplete.setEnabled(false);
    }

    public void ShowFinishButton()
    {
        buttonComplete.setVisibility(View.VISIBLE);
        buttonComplete.setEnabled(true);
    }
}
