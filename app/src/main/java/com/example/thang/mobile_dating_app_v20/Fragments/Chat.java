package com.example.thang.mobile_dating_app_v20.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Thang on 5/27/2015.
 */
public class Chat extends Fragment{
    private static final String URL_SERVER = "http://datingappservice2.groundctrl.nl/datingapp/Service/chat";
    private static final int PORT = 3333;
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.chat,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        Person friend = gson.fromJson(bundle.getString("json"),Person.class);
        try {
            socket = new Socket(URL_SERVER,PORT);
            InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamreader);
            writer = new PrintWriter(socket.getOutputStream());
            writer.write("name" + ":message" + ":status");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class chattingThread implements Runnable{

        @Override
        public void run() {

        }
    }
}
