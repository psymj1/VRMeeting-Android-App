package hexcore.vrgui;

import android.os.AsyncTask;

import com.ibm.watson.developer_cloud.personality_insights.v2.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v2.model.Profile;

import java.util.List;

import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class WatsonCallAsync extends AsyncTask<String, Void, String> {

    /**
     * configures access to twitter api
     * reads tweets repeatedly for 100 pages
     * sets up watson api call and passes string of tweets to watson for analysing
     * @param params
     * @return
     */

    @Override
    protected String doInBackground(String... params) {
        String profileInput = "";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("")
                .setOAuthConsumerSecret("")
                .setOAuthAccessToken("")
                .setOAuthAccessTokenSecret("");

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        try {
            String user = params[0];
            List<twitter4j.Status> statuses;
            Paging paging = new Paging(1, 100);
            statuses = twitter.getUserTimeline(user, paging);

            for (twitter4j.Status status : statuses) {
                profileInput += "" + status.getText();
            }

        } catch (TwitterException te) {
            te.printStackTrace();
        }

        if (!profileInput.equals(""))
        {
            PersonalityInsights service = new PersonalityInsights();
            service.setUsernameAndPassword("", "");
            service.setEndPoint("https://gateway.watsonplatform.net/personality-insights/api");
            Profile profile = service.getProfile(profileInput);
            String percentile = "";
            JSONObject json = new JSONObject(profile);
            try {
                JSONObject tree = json.getJSONObject("tree");
                twitter4j.JSONArray child1 = tree.getJSONArray("children");
                JSONObject personality = child1.getJSONObject(0);
                twitter4j.JSONArray child2 = personality.getJSONArray("children");
                JSONObject value1 = child2.getJSONObject(0);

                percentile = value1.getString("percentage");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return percentile;
        }
        else
            return "-1";
    }

}
