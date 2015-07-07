package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.ChatFriendAdapter;
import com.example.thang.mobile_dating_app_v20.Chat.ChatJSON;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends ActionBarActivity {

    public static final String SOCKET_SERVER_ADDRESS = "192.168.43.179";
    private static final int SOCKET_SERVER_PORT = 8084;

    ListView chatPanel;
    MaterialEditText chatMessage;
    ImageButton send;
    String msgToSend;
    SocketClient socketClient;

    List<Message> messages = new ArrayList<>();
    ChatFriendAdapter friendAdapter;
    String avatarFriend;
    String avatarMe;

    //test only
    static int count = 1;

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
        //set avatar param
        avatarFriend = bundle.getString("Avatar");
        avatarMe = DBHelper.getInstance(this).getCurrentUser().getAvatar();

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

                if (charSequence.length() != 0) {
                    send.setImageResource(R.drawable.ic_action_send_active);
                    send.setClickable(true);
                } else {
                    send.setImageResource(R.drawable.ic_action_send);
                    send.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //set onclicklistener for send button
        send.setOnClickListener(buttonSendOnClickListener);

        //apply chat adapter
        friendAdapter = new ChatFriendAdapter(messages, this);
        chatPanel.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatPanel.setAdapter(friendAdapter);

        //to scroll the listview to bottom on data change
        friendAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatPanel.setSelection(friendAdapter.getCount() - 1);
                    }
                });
            }
        });

        //initial client socket
        String email = DBHelper.getInstance(this).getCurrentUser().getEmail();
        socketClient = new SocketClient(SOCKET_SERVER_ADDRESS, SOCKET_SERVER_PORT, email, bundle.getString("Email"));
        socketClient.start();

    }

    View.OnClickListener buttonSendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            socketClient.sendMessage(chatMessage.getText().toString());
            chatMessage.setText("");
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

    @Override
    public void onBackPressed() {
        //disconnect from server after destroy
        socketClient.disConnect();
        super.onBackPressed();
    }

    //Socket Client Class
    public class SocketClient extends Thread {
        String email;
        boolean isClose;
        String msgToSend;
        String serverAddress;
        int port;
        String receiver;
        Context context;

        public SocketClient(String serverAddress, int port, String email, String receiver) {
            this.serverAddress = serverAddress;
            this.port = port;
            this.email = email;
            this.receiver = receiver;
            this.isClose = false;
            this.msgToSend = "";
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                // Make connection and initialize streams
                socket = new Socket(serverAddress, port);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                //Establish connection for the 1st time
                String establish = ChatJSON.setClientEstablish(email);
                dataOutputStream.writeUTF(establish);
                dataOutputStream.flush();

                while (!isClose) {
                    if (dataInputStream.available() > 0) {
                        String input = dataInputStream.readUTF();
                        //display input from server
                        Log.i(null, input);
                        Message message = new Message(input, avatarFriend, "", Message.CHAT_FRIEND_ITEM);
                        messages.add(message);
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                    if (!msgToSend.equals("")) {
                        //display send message
                        Log.i(null, email + ": " + msgToSend);
                        Message message = new Message(msgToSend, avatarMe, "", Message.CHAT_ME_ITEM);
                        messages.add(message);
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendAdapter.notifyDataSetChanged();
                            }
                        });
                        //create message json
                        String output = ChatJSON.setClientMessage(email, receiver, msgToSend);
                        //send message to server
                        dataOutputStream.writeUTF(output);
                        dataOutputStream.flush();
                        //reset message variable
                        msgToSend = "";
                    }
                }
                //send Exit JSON to server
                System.out.println("Disconnected from server!");
                String exit = ChatJSON.setClientDisconnect();
                dataOutputStream.writeUTF(exit);
                dataOutputStream.flush();

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void sendMessage(String message) {
            this.msgToSend = message;
        }

        public void disConnect() {
            this.isClose = true;
        }
    }

}
