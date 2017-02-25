package io.lattis.hitme.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.lattis.hitme.model.ChatBox;
import io.lattis.hitme.model.ChatNode;
import io.realm.Realm;

public final class QueryUtils {

    static Realm realm;


    private static final String TAG = "QueryUtils";


    private QueryUtils() {
    }

    public static void fetchChatData(String requestUrl) throws JSONException {
        Log.e(TAG, "fetchEarthquakeData: ");

        // Testing Indeterminant ProgressBar
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }

        retrieveChatNodesFromResponse(jsonResponse);

    }


    public static void retrieveChatNodesFromResponse(String response) throws
            JSONException {

        // Beware of static
        realm = Realm.getDefaultInstance();
        // TODO Close realm!!


        final JSONObject fullResponse = new JSONObject(response);
        final JSONArray jsonArray = fullResponse.getJSONArray("payload");

        realm.executeTransaction(new Realm.Transaction()  {
            @Override
            public void execute(Realm realm)  {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ChatNode chatNode = realm.createObject(ChatNode.class);

                        JSONObject jsonNode = null;
                        jsonNode = jsonArray.getJSONObject(i);

                        chatNode.setContact(jsonNode.getString("sender"));

                        JSONArray jsonMsgs = jsonNode.getJSONArray("messages");
                        for (int j = 0; j < jsonMsgs.length(); j++) {

                            JSONObject jsonNodeMessage = jsonMsgs.getJSONObject(j);

                            ChatBox currentChatBox = realm.createObject(ChatBox.class);
                            currentChatBox.setSpeaker(jsonNodeMessage.getString("sender"));
                            currentChatBox.setMessage(jsonNodeMessage.getString("message"));
                            currentChatBox.setTimeSent(jsonNodeMessage.getString("time_sent"));

                            chatNode.getChatBox().add(currentChatBox);
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }



        });




    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL ", e);
        }

        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        Log.d(TAG, "makeHttpRequest: Hii ");
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/* milliSeconds */);
            urlConnection.setConnectTimeout(1500/* milliSeconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the chats JSON results.", e);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (inputStream != null)
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
        }

        Log.d(TAG, "makeHttpRequest: " + jsonResponse);
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                // What!!
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");

    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static String getCurrentDate() {

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    public static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);


    }


}

