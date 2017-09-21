package com.aman.freechat.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aman.freechat.R;
import com.aman.freechat.adapters.ChatAdapter;
import com.aman.freechat.db.ChatDB;
import com.aman.freechat.model.Chat;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.NotificationUtil;
import com.aman.freechat.utils.SharedPrefHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ROOM_NUM = "room_number";
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_ID = "friend_id";
    public static final String POS = "position";
    public static final String CHAT = "chat";
    public static final String TIMESTAMP = "timestamp";
    public static final String FRIEND_TOKEN = "token";

    public static boolean isShown;

    private static final String TAG = ChatActivity.class.getName();
    private RecyclerView recycler_chat;
    private EditText message;
    private FloatingActionButton send;
    private String roomNum, name, friend_id, friend_token;
    private DatabaseReference reference;
    private ArrayList<Chat> chatList;
    private ChatAdapter adapter;

    private int position_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra(FRIEND_NAME);
            roomNum = getIntent().getStringExtra(ROOM_NUM);
            friend_id = getIntent().getStringExtra(FRIEND_ID);
            friend_token = getIntent().getStringExtra(FRIEND_TOKEN);
            position_clicked = getIntent().getIntExtra(POS, -1);
        }

        reference = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();
    }

    private void initViews() {
        chatList = new ArrayList<>();

        recycler_chat = (RecyclerView) findViewById(R.id.recycler_chat);
        recycler_chat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this, chatList);
        recycler_chat.setAdapter(adapter);

        message = (EditText) findViewById(R.id.message);
        send = (FloatingActionButton) findViewById(R.id.send);

        send.setOnClickListener(this);


        if (!AppUtility.isNetworkAvailable(this)) {
            getFromLocal();
        } else {
            reference.child("message/" + roomNum).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        chatList.add(chat);
                        adapter.notifyItemInserted(chatList.size() - 1);
                        recycler_chat.smoothScrollToPosition(chatList.size() - 1);

                        if (ChatDB.getInstance().getAllChats(roomNum).size() < chatList.size()) {
                            ChatDB.getInstance().saveMessage(chat);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFromLocal() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtility.showDialog(ChatActivity.this);
                chatList.clear();
            }

            @Override
            protected Void doInBackground(Void... params) {
                Log.e(TAG, "onPostExecute: " + ChatDB.getInstance().getAllChats(roomNum).size());
                chatList.addAll(ChatDB.getInstance().getAllChats(roomNum));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                adapter.notifyDataSetChanged();
                if (chatList.size() != 0)
                    recycler_chat.smoothScrollToPosition(chatList.size() - 1);
                AppUtility.dismissDialog();
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                String msg = message.getText().toString();
                if (msg.length() > 0) {
                    message.setText("");
                    Chat chat = new Chat();
                    chat.idReceiver = SharedPrefHelper.getInstance(this).getUserInfo().id;
                    chat.idSender = friend_id;
                    chat.chatRoomId = roomNum;
                    chat.message = msg;
                    chat.timeStamp = System.currentTimeMillis();

                    sendNotification(friend_token, chat.message, name);

                    reference.child("message/" + roomNum).push().setValue(chat);
                } else {
                    message.setError("Enter a message to send");
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            if (chatList.size() > 0) {
                intent.putExtra(POS, position_clicked);
                intent.putExtra(CHAT, chatList.get(chatList.size() - 1).message);
                intent.putExtra(TIMESTAMP, chatList.get(chatList.size() - 1).timeStamp);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (chatList.size() > 0) {
            intent.putExtra(POS, position_clicked);
            intent.putExtra(CHAT, chatList.get(chatList.size() - 1).message);
            intent.putExtra(TIMESTAMP, chatList.get(chatList.size() - 1).timeStamp);
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void sendNotification(String id_token, String message, String name) {
        NotificationUtil.getInstance().sendNotification(id_token, message, name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isShown = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShown = false;
    }
}
