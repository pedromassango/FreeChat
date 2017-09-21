package com.aman.freechat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Message;

import com.aman.freechat.model.Chat;

import java.util.ArrayList;

/**
 * Created by aman on 19/9/17.
 */

public class ChatDB {
    private static ChatDB instance;

    private static final String ROOM_NUM = "roomNum";
    private static final String ID_SENDER = "idSender";
    private static final String ID_RECEIVER = "idReceiver";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";

    private static final String CHAT_TABLE_NAME = "chat";

    private ArrayList<Chat> chatList = new ArrayList<>();

    private ChatDB() {
    }

    public static ChatDB getInstance() {
        if(instance == null) {
            instance = new ChatDB();
        }
        return instance;
    }

    public boolean saveMessage(Chat chat) {
        ContentValues values = new ContentValues();
        values.put(ROOM_NUM, chat.chatRoomId);
        values.put(ID_SENDER, chat.idSender);
        values.put(ID_RECEIVER, chat.idReceiver);
        values.put(MESSAGE, chat.message);
        values.put(TIMESTAMP, chat.timeStamp);

        return Db.getInstance().insert(CHAT_TABLE_NAME, null, values) > 0;
    }

    public Chat getLastMessage(String roomNum) {
        Cursor cursor = Db.getInstance().rawQuery("select * from " + CHAT_TABLE_NAME
                + " where "
                + ROOM_NUM
                + "=" + roomNum
                + " order by timestamp desc limit 1");
        if(cursor.moveToFirst()) {
            Chat chat = new Chat();
            chat.chatRoomId = cursor.getString(0);
            chat.idReceiver = cursor.getString(1);
            chat.idSender = cursor.getString(2);
            chat.message = cursor.getString(3);
            chat.timeStamp = cursor.getLong(4);

            return chat;
        }

        return null;
    }

    public void dropDB() {
        Db.getInstance().beginTransaction();
        Db.getInstance().delete("chat", null, null);
        Db.getInstance().setTransactionSuccessful();
        Db.getInstance().endTransaction();
    }

    public ArrayList<Chat> getAllChats(String roomNum) {
        if(chatList.size() > 0)
            chatList.clear();

        Cursor cursor = Db.getInstance().rawQuery("select * from " + CHAT_TABLE_NAME
                + " where "
                + ROOM_NUM
                + "=" + roomNum);
        while (cursor.moveToNext()) {
            Chat chat = new Chat();
            chat.chatRoomId = cursor.getString(0);
            chat.idReceiver = cursor.getString(1);
            chat.idSender = cursor.getString(2);
            chat.message = cursor.getString(3);
            chat.timeStamp = cursor.getLong(4);

            chatList.add(chat);
        }

        return chatList;
    }
}
