package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Fragments.Chat;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatActivity extends ActionBarActivity {
    static final int SOCKET_SERVER_PORT = 8089;
    ListView chatPanel;
    MaterialEditText chatMessage;
    ImageButton send;

    ChatThread chatThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get FullName to set title for activity
        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(bundle.getString("FullName"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //view initial
        chatPanel = (ListView) findViewById(R.id.chat_panel);
        chatMessage = (MaterialEditText) findViewById(R.id.chat_message);
        send = (ImageButton) findViewById(R.id.send_message);

        chatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 0){
                    send.setImageResource(R.drawable.ic_action_send_active);
                    send.setClickable(true);
                }else{
                    send.setImageResource(R.drawable.ic_action_send);
                    send.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //start chat thread
        chatThread = new ChatThread(bundle.getString("FullName"), "", SOCKET_SERVER_PORT);
        chatThread.start();

        //set onclicklistener for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),chatMessage.getText(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    View.OnClickListener buttonSendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (chatMessage.getText().toString().equals("")){
                return;
            }

            if (chatThread == null){
                return;
            }

            chatThread.sendMsg(chatMessage.getText().toString() + "\n");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ChatThread extends Thread {
        String name;
        String dstAddress;
        int dstPort;

        String msgToSend = "";
        boolean goOut = false;

        ChatThread( String name, String address, int port) {
            this.name = name;
            this.dstAddress = address;
            this.dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                while (!goOut) {
                    if (dataInputStream.available() > 0) {
                        String message = dataInputStream.readUTF();
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //show message text
                            }
                        });
                    }
                    if (!msgToSend.equals("")) {
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }
                }
            }  catch (UnknownHostException e) {
                e.printStackTrace();
                final String eString = e.toString();
                ChatActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                ChatActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, eString, Toast.LENGTH_LONG).show();
                    }

                });
            }finally {
                if (socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null){
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null){
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

        private void disconnect(){
            goOut = true;
        }

    }


}
