package com.example.thang.mobile_dating_app_v20.Chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thang on 7/6/2015.
 */
public class ChatJSON {

    private static final String FLAG_ESTABLISH = "first";
    private static final String FLAG_MESSAGE = "message";
    private static final String FLAG_EXIT = "exit";

    public ChatJSON() {
    }

    public static String setClientEstablish(String email, String to) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_ESTABLISH);
            jObj.put("email", email);
            jObj.put("to", to);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String setClientMessage(String email, String to, String message) {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("email", email);
            jObj.put("to", to);
            jObj.put("message", message);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String setClientDisconnect() {
        String json = null;
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_EXIT);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
