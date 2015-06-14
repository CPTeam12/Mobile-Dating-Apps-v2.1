package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;
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
                if (jsonObject2 != null){
                    Person person = new Person();
                    person.setAge(Integer.parseInt(jsonObject2.getString("age")));
                    person.setFullName(jsonObject2.getString("fullName"));
                    person.setGender(jsonObject2.getString("gender"));
                    person.setEmail(jsonObject2.getString("email"));
                    person.setPhone(jsonObject2.getString("phone"));

                    person.setLastKnown(Integer.parseInt(jsonObject2.getString("lastKnown")));
                    person.setLatitude(Double.parseDouble(jsonObject2.getString("latitude")));
                    person.setLongitude(Double.parseDouble(jsonObject2.getString("longitude")));

                    person.setHobbies(jsonObject2.getString("hobbies"));
                    person.setDatingMen(Boolean.getBoolean(jsonObject2.getString("datingMen")));
                    person.setDatingWomen(Boolean.getBoolean(jsonObject2.getString("datingWomen")));
                    person.setDatingAge(Integer.parseInt(jsonObject2.getString("datingAge")));

                    persons.add(person);
                }
            } else {
                JSONArray jsonArray = new JSONArray(data);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                    Person person = new Person();
                    person.setAge(Integer.parseInt(jsonObject1.getString("age")));
                    person.setFullName(jsonObject1.getString("fullName"));
                    person.setGender(jsonObject1.getString("gender"));
                    person.setEmail(jsonObject1.getString("email"));
                    person.setPhone(jsonObject1.getString("phone"));

                    person.setLastKnown(Integer.parseInt(jsonObject1.getString("lastKnown")));
                    person.setLatitude(Double.parseDouble(jsonObject1.getString("latitude")));
                    person.setLongitude(Double.parseDouble(jsonObject1.getString("longitude")));

                    person.setHobbies(jsonObject1.getString("hobbies"));
                    person.setDatingMen(Boolean.getBoolean(jsonObject1.getString("datingMen")));
                    person.setDatingWomen(Boolean.getBoolean(jsonObject1.getString("datingWomen")));
                    person.setDatingAge(Integer.parseInt(jsonObject1.getString("datingAge")));

                    persons.add(person);
                }
            }
        } else if (statusCode == 1) {
            return null;
        }
        return persons;
    }

    public static String makePostRequest(String url, List<Person> persons){
        HttpURLConnection urlConnection;
        String result = "";
        String data = "";

        Gson gson = new Gson();
        data = gson.toJson(persons);

        try {
            //connect
            urlConnection = (HttpURLConnection)(new URL(url).openConnection());
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String makeGetRequest(String url){
        HttpURLConnection urlConnection;
        String result = null;
        try {
            //Connect
            urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            String line = null;
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }
}
