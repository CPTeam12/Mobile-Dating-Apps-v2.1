package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/31/2015.
 */
public class ConnectionTool implements Serializable {
    private static Context context;

    public ConnectionTool(){

    }
    public ConnectionTool(Context context) {
        this.context = context;
    }

    public static InputStream openHttpConnection(String urlString) throws IOException {
        InputStream is = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();




        if (!(con instanceof HttpURLConnection)) {
            throw new IOException("Http connection is required");
        }
        try {
            HttpURLConnection httpCon = (HttpURLConnection) con;
            httpCon.setAllowUserInteraction(false);
            httpCon.setInstanceFollowRedirects(true);
            httpCon.setRequestMethod("GET");
            httpCon.connect();
            response = httpCon.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                is = httpCon.getInputStream();
            }

        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("ERROR CONNECTING");
        }
        return is;
    }

    public static boolean isConnectedToServer(String url,int timeout){
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String downloadText(String url) {
        int buffer_size = 2000;
        InputStream is = null;
        try {
            is = openHttpConnection(url);
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }

        InputStreamReader isr = new InputStreamReader(is);
        int charRead;
        String result = "";
        char[] inputBuffer = new char[buffer_size];
        try {
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                result += readString;
                inputBuffer = new char[buffer_size];
            }
            is.close();
        } catch (IOException e) {
            Log.d("Networking-process", e.getLocalizedMessage());
            return "";
        }
        return result;
    }

    public static String readJSONFeed(String url) {
        StringBuilder sb = new StringBuilder();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                Log.e("JSON", "Fail to download");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("JSON", sb.toString());
        return sb.toString();
    }

    public static List<Person> fromJSON(JSONObject jsonObject) throws JSONException {
        List<Person> persons = new ArrayList<>();
        //get MDAResponse JSON
        int statusCode = Integer.parseInt(jsonObject.getString("statusCode"));
        String message = jsonObject.getString("message");
        //check status code
        //0 - success
        //1 - fail
        if (statusCode == 0) {
            //Start get JSON in data tag
            String data = jsonObject.getString("data");
            Object object = new JSONTokener(data).nextValue();
            if (object instanceof JSONObject) {
                JSONObject jsonObject2 = jsonObject.getJSONObject("data");
                String fullname = jsonObject2.getString("fullName");
                //String username = jsonObject2.getString("username");
                int age = Integer.parseInt(jsonObject2.getString("age"));
                String email = jsonObject2.getString("email");
                String gender = jsonObject2.getString("gender");
                String password = jsonObject2.getString("password");
                Person person = new Person(password, fullname, email, age, gender);
                persons.add(person);
            } else {
                JSONArray jsonArray = new JSONArray(data);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                    String fullname = jsonObject1.getString("fullName");
                    //String username = jsonObject1.getString("username");
                    int age = Integer.parseInt(jsonObject1.getString("age"));
                    String email = jsonObject1.getString("email");
                    String gender = jsonObject1.getString("gender");
                    String password = jsonObject1.getString("password");
                    Person person = new Person(password, fullname, email, age, gender);
                    persons.add(person);
                }
            }
        } else if (statusCode == 1) {
            return null;
        }
        return persons;
    }
}
