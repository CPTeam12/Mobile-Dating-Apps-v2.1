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
                    String fullname = jsonObject2.getString("fullName");
                    //String username = jsonObject2.getString("username");
                    int age = Integer.parseInt(jsonObject2.getString("age"));
                    String email = jsonObject2.getString("email");
                    String gender = jsonObject2.getString("gender");
                    String password = jsonObject2.getString("password");
                    Person person = new Person(password, fullname, email, age, gender);
                    persons.add(person);
                }
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

    public static String makePostRequest(String url, List<Person> persons){
        HttpURLConnection urlConnection;
        String result = null;
        String data = null;

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
