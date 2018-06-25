package hexcore.vrgui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SavedContacts extends AppCompatActivity {

    /**
     * Method used to initialise the activity instance
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_contacts);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
    }

    /**
     * Displays a table with all of the saved contacts the user has
     * When a saved contact's name is pressed, the details of that user is displayed in a new activity
     */

    public void init()
    {
        //retreiveSavedContacts();
        TableLayout table = findViewById(R.id.table);

        for (int i = 0; i <2; i++) { //Replace 2 to the number of entries in the database
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            TextView newEntryName = new TextView(this);
            TextView newEntryComp = new TextView(this);
            newEntryName.setText("Hello"); //Replace with the contact's name retrieved from the server call
            newEntryComp.setText("Bye"); //Replace with the contact's company retrieved from the server call
            newEntryName.setTextColor(Color.WHITE);
            newEntryComp.setTextColor(Color.WHITE);
            newEntryName.setGravity(Gravity.CENTER_HORIZONTAL);
            newEntryComp.setGravity(Gravity.CENTER_HORIZONTAL);
            newEntryName.setTextSize(20);
            newEntryComp.setTextSize(20);
            newEntryName.setLayoutParams(new TableRow.LayoutParams(196,WRAP_CONTENT));
            newEntryComp.setLayoutParams(new TableRow.LayoutParams(196,WRAP_CONTENT));
            newEntryName.isClickable();
            newEntryName.setPaintFlags(newEntryName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            final ArrayList<String> contInfo = new ArrayList<String>();
            contInfo.add((String)newEntryName.getText());
            contInfo.add((String)newEntryComp.getText());
            newEntryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SavedContacts.this, ViewContDetails.class);
                    intent.putStringArrayListExtra("contInfo", contInfo);
                    startActivity(intent);
                }
            });
            row.addView(newEntryName);
            row.addView(newEntryComp);
            table.addView(row);
        }
    }

    /**
     * GET request to retrieve all the saved contacts for the user
     */

    public void retreiveSavedContacts(/*Token?*/) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + Config.getWebServerURL(this) + "/savedContacts";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String contactName = null;
                String contactComp = null;
                try {
                    contactName = (String) jsonObject.get("contactName");
                    contactComp = (String) jsonObject.get("contactCompany");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SERVER RESPONSE:", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;
        queue.add(stringRequest);
    }
}
