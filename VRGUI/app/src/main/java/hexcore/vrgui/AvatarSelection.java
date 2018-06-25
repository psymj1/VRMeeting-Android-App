package hexcore.vrgui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AvatarSelection extends AppCompatActivity {

    ViewPager viewPager;
//    EditText twitterID;
//    Button generate;
    ArrayList<String> userInfo;

    /**
     * Method used to initialise the activity instance
     * The ViewPager for the avatar selection slider is added to this instance so that it can be displayed
     * Depending on the user's selected gender, the slider will display the relevant gender avatars
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_selection);
        userInfo = getIntent().getExtras().getStringArrayList("userInfo");
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

//        twitterID = findViewById(R.id.txtTwitter);
//        generate = findViewById(R.id.btnTwitter);
//        twitterID.setError( "Using IBM Watson we can analyse your tweets from your Twitter account to generate a model that suits you!", null);
        if (userInfo.get(9).equals("Male"))
        {
            viewPager.setCurrentItem(0);
        }
        else
        {
            viewPager.setCurrentItem(5);
        }
    }

    /**
     * Determines if field is empty before analysing
     * Runs separate thread to start auto avatar selection
     * Checks if user is valid, if not display so
     * When thread finishes, display the appropriate avatar if value is within certain threshold
     * @param view
     * @deprecated
     */

//    public void onPressWatson(View view) {
//        if (twitterID.length() == 0) {
//            twitterID.requestFocus();
//            twitterID.setError("Field cannot be empty!");
//        } else {
//            final String userID = twitterID.getText().toString();
//            String receivedData = "";
//            try {
//                receivedData = new WatsonCallAsync().execute(userID).get();
//            }
//            catch (ExecutionException | InterruptedException ei) {
//                ei.printStackTrace();
//            }
//
//            if (receivedData.equals("-1")){
//                twitterID.requestFocus();
//                twitterID.setError("User not found!");
//            }
//            else if (receivedData.equals(""))
//            {
//                twitterID.requestFocus();
//                twitterID.setError("Connection Error, select an avatar manually");
//            }
//            else
//            {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
//
//                Double rawValue = 0.0;
//                int convValue = 0;
//                String valueS = receivedData.substring(0,4);
//                try{
//                    rawValue = Double.parseDouble(valueS);
//                    rawValue = rawValue * 100;
//                    convValue = rawValue.intValue();
//                }catch(NumberFormatException ex){ // handle exception
//                }
//
//                if (userInfo.get(9).equals("Male"))
//                {
//                    if (convValue < 20)
//                        viewPager.setCurrentItem(0);
//                    else if (convValue < 40)
//                        viewPager.setCurrentItem(1);
//                    else if (convValue < 60)
//                        viewPager.setCurrentItem(2);
//                    else if (convValue < 80)
//                        viewPager.setCurrentItem(3);
//                    else
//                        viewPager.setCurrentItem(4);
//                }
//                else
//                {
//                    if (convValue < 20)
//                        viewPager.setCurrentItem(5);
//                    else if (convValue < 40)
//                        viewPager.setCurrentItem(6);
//                    else if (convValue < 60)
//                        viewPager.setCurrentItem(7);
//                    else if (convValue < 80)
//                        viewPager.setCurrentItem(8);
//                    else
//                        viewPager.setCurrentItem(9);
//                }
//            }
//        }
//    }

    /**
     * Function to disable the phone's back button
     */

    @Override
    public void onBackPressed() {
    }

    /**
     * When 'Select' is pressed, the avatar id selected gets stored in the Array List so that it can be stored on the server
     * The avatar IDs start from 0 to 9 and are in the order of the image slider
     * Once it is stored in the Array List, the security questions page is displayed
     * @param view
     */

    public void onPressNext(View view) {
        int id = viewPager.getCurrentItem();
        userInfo.add(Integer.toString(id));
        Intent intent = new Intent(AvatarSelection.this, SecurityQuestions.class);
        intent.putStringArrayListExtra("userInfo", userInfo);
        startActivity(intent);
    }
}
