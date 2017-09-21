package com.aman.freechat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.aman.freechat.model.Chat;
import com.aman.freechat.model.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aman on 15/9/17.
 */

public class FriendDB {
    private static FriendDB instance;

    private static final String FRIEND_ID = "id";
    private static final String FRIEND_NAME = "name";
    private static final String FRIEND_IMAGE = "image";
    private static final String FRIEND_EMAIL = "email";
    private static final String FRIEND_ROOM_NUM = "roomNum";

    private static final String FRIEND_TABLE_NAME = "friends";

    private List<Friend> friendList;

    private FriendDB() {
    }

    public static FriendDB getInstance() {
        if(instance == null) {
            instance = new FriendDB();
        }

        return instance;
    }

    public void dropDB() {
        Db.getInstance().beginTransaction();
        Db.getInstance().delete("friends", null, null);
        Db.getInstance().setTransactionSuccessful();
        Db.getInstance().endTransaction();
    }

    public boolean addFriend(Friend friend) {
        ContentValues values = new ContentValues();
        values.put(FRIEND_ID, friend.id);
        values.put(FRIEND_NAME, friend.userName);
        values.put(FRIEND_IMAGE, friend.image);
        values.put(FRIEND_EMAIL, friend.email);
        values.put(FRIEND_ROOM_NUM, friend.roomNum);

        return Db.getInstance().insert(FRIEND_TABLE_NAME, null, values) > 0;
    }

    public void addFriendList(List<Friend> list) {
        for(Friend friend : list) {
            addFriend(friend);
        }
    }

    public List<Friend> getAllFriends() {
        //if(friendList == null) {
            friendList = new ArrayList<>();

            Cursor cursor = Db.getInstance().rawQuery("select * from " + FRIEND_TABLE_NAME);
            while(cursor.moveToNext()) {
                Friend friend = new Friend();
                friend.id = cursor.getString(0);
                friend.userName = cursor.getString(1);
                friend.image = cursor.getString(2);
                friend.email = cursor.getString(3);
                friend.roomNum = cursor.getString(4);

                Chat chat = ChatDB.getInstance().getLastMessage(friend.roomNum);

                if(chat != null) {
                    friend.lastMessage = ChatDB.getInstance().getLastMessage(friend.roomNum).message;
                    friend.timeStamp = ChatDB.getInstance().getLastMessage(friend.roomNum).timeStamp;
                }

                friendList.add(friend);
            }
        //}

        return friendList;
    }
}
