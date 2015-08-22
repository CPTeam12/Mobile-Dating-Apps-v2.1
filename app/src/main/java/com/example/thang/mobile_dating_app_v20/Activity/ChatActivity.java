package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Adapters.ChatFriendAdapter;
import com.example.thang.mobile_dating_app_v20.Chat.ChatJSON;
import com.example.thang.mobile_dating_app_v20.Classes.ChatItem;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends ActionBarActivity {

    private static final int SOCKET_SERVER_PORT = 8085;

    ListView chatPanel;
    MaterialEditText chatMessage;
    ImageButton send;
    String msgToSend;
    SocketClient socketClient;
    String currentUserEmail;
    View noChatMessage;

    List<Message> messages = new ArrayList<>();
    List<ChatItem> chatItems = new ArrayList<>();

    ChatFriendAdapter friendAdapter;
    String avatarFriend;
    String avatarMe;
    int conversationId;
    String friendChatEmail;
    String currentDate = getChatDateOnly();
    String lastDivider = "";
    //Test only
    static int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get FullName to set title for activity
        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        //get friend details
        Person friend = DBHelper.getInstance(this).getPersonByEmail(bundle.getString("Email"));

        toolbar.setTitle(friend.getFullName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //set avatar param

        avatarFriend = friend.getAvatar();
        avatarMe = DBHelper.getInstance(this).getCurrentUser().getAvatar();

        //view initial
        chatPanel = (ListView) findViewById(R.id.chat_panel);
        chatMessage = (MaterialEditText) findViewById(R.id.chat_message);
        noChatMessage = findViewById(R.id.no_chat_item);

        send = (ImageButton) findViewById(R.id.send_message);

        chatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() != 0) {
                    send.setImageResource(R.drawable.ic_action_send_active);
                    //send.setColorFilter(Color.parseColor("#D32F2F"));
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

        //check for chat history
        currentUserEmail = DBHelper.getInstance(this).getCurrentUser().getEmail();
        friendChatEmail = bundle.getString("Email");
        conversationId = DBHelper.getInstance(this).getConservationId(currentUserEmail, friendChatEmail);
        if (conversationId != -1) {
            messages = DBHelper.getInstance(this).getReplyByConversationId(conversationId);
        } else {
            //insert new conversation
            DBHelper.getInstance(this).insertConversation(currentUserEmail, friendChatEmail);
            conversationId = DBHelper.getInstance(this).getConservationId(currentUserEmail, friendChatEmail);
        }

        //apply chat adapter
        for (Message message : messages) {
            //format time
            String[] date = message.getTime().split(" ");
            String dateOnly = date[0];
            String timeOnly = date[1];
            //currentDate = getChatDateOnly();

            if (!TextUtils.equals(lastDivider, dateOnly)) {
                //insert new time divider
                if (currentDate.equals(dateOnly)) {
                    chatItems.add(new ChatItem(message, true, getResources().getString(R.string.chat_today)));
                } else {
                    chatItems.add(new ChatItem(message, true, dateOnly));
                }
                lastDivider = dateOnly;
            }
            chatItems.add(new ChatItem(message, false, timeOnly));
        }

        //show empty state
        if (messages.isEmpty()) {
            noChatMessage.setVisibility(View.VISIBLE);
        }

        friendAdapter = new ChatFriendAdapter(chatItems, this);
        chatPanel.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatPanel.setAdapter(friendAdapter);
        chatPanel.setSelection(messages.size() - 1);

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
        socketClient = new SocketClient(MainActivity.IP, SOCKET_SERVER_PORT, email, bundle.getString("Email"));
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

        // Handle item selection
        switch (id) {
            case R.id.chat_notification:
                Toast.makeText(getApplicationContext(), "Notification disabled", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.chat_delete_conversation:
                //delete conversation from SQLite
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                dbHelper.deleteConversationById(conversationId);
                dbHelper.deleteReplyByConversationId(conversationId);
                //empty messages
                messages.clear();
                chatItems.clear();
                lastDivider = "";
                friendAdapter.notifyDataSetChanged();
                //show empty state
                noChatMessage.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Conversation is emptied", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                String establish = ChatJSON.setClientEstablish(email, receiver);
                dataOutputStream.writeUTF(establish);
                dataOutputStream.flush();

                while (!isClose) {

                    if (dataInputStream.available() > 0) {
                        String input = dataInputStream.readUTF();
                        //display input from server
                        Log.i(null, input);
                        Message message = new Message(input, avatarFriend, getTime(), Message.CHAT_FRIEND_ITEM);
                        messages.add(message);

                        String[] date = message.getTime().split(" ");
                        String dateOnly = date[0];
                        String timeOnly = date[1];

                        if (!TextUtils.equals(lastDivider, dateOnly)) {
                            //insert new time divider
                            if (currentDate.equals(dateOnly)) {
                                chatItems.add(new ChatItem(message, true, getResources().getString(R.string.chat_today)));
                            } else {
                                chatItems.add(new ChatItem(message, true, dateOnly));
                            }
                            lastDivider = dateOnly;
                        }
                        chatItems.add(new ChatItem(message, false, timeOnly));

                        //check conversation is existed or not
                        conversationId = DBHelper.getInstance(getApplicationContext()).getConservationId(currentUserEmail, friendChatEmail);
                        if (conversationId != -1) {
                            messages = DBHelper.getInstance(getApplicationContext()).getReplyByConversationId(conversationId);
                        } else {
                            //insert new conversation
                            DBHelper.getInstance(getApplicationContext()).insertConversation(currentUserEmail, friendChatEmail);
                            conversationId = DBHelper.getInstance(getApplicationContext()).getConservationId(currentUserEmail, friendChatEmail);
                        }
                        //insert chat message to SQLite
                        DBHelper.getInstance(getApplicationContext())
                                .insertConversationMessage(friendChatEmail, input, conversationId, getTime());
                        //update message list
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendAdapter.notifyDataSetChanged();
                                //check empty state
                                if (!chatItems.isEmpty()) {
                                    noChatMessage.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                    if (!msgToSend.equals("")) {
                        //display send message
                        Log.i(null, email + ": " + msgToSend);
                        Message message = new Message(msgToSend, avatarMe, getTime(), Message.CHAT_ME_ITEM);
                        messages.add(message);

                        String[] date = message.getTime().split(" ");
                        String dateOnly = date[0];
                        String timeOnly = date[1];

                        if (!TextUtils.equals(lastDivider, dateOnly)) {
                            //insert new time divider
                            if (currentDate.equals(dateOnly)) {
                                chatItems.add(new ChatItem(message, true, getResources().getString(R.string.chat_today)));
                            } else {
                                chatItems.add(new ChatItem(message, true, dateOnly));
                            }
                            lastDivider = dateOnly;
                        }
                        chatItems.add(new ChatItem(message, false, timeOnly));
                        //check conversation is existed or not
                        conversationId = DBHelper.getInstance(getApplicationContext()).getConservationId(currentUserEmail, friendChatEmail);
                        if (conversationId != -1) {
                            messages = DBHelper.getInstance(getApplicationContext()).getReplyByConversationId(conversationId);
                        } else {
                            //insert new conversation
                            DBHelper.getInstance(getApplicationContext()).insertConversation(currentUserEmail, friendChatEmail);
                            conversationId = DBHelper.getInstance(getApplicationContext()).getConservationId(currentUserEmail, friendChatEmail);
                        }
                        //insert chat message to SQLite
                        DBHelper.getInstance(getApplicationContext())
                                .insertConversationMessage(DBHelper.getInstance(getApplicationContext()).getCurrentUser().getEmail(),
                                        msgToSend, conversationId, getTime());
                        //update message list
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendAdapter.notifyDataSetChanged();
                                //check empty state
                                if (!chatItems.isEmpty()) {
                                    noChatMessage.setVisibility(View.GONE);
                                }
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

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getChatTimeOnly() {
        String[] time = getTime().split(" ");
        return time[1];
    }

    private String getChatDateOnly() {
        String[] time = getTime().split(" ");
        return time[0];
    }

}
