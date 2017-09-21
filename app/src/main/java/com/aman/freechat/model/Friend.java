package com.aman.freechat.model;

import com.google.firebase.database.Exclude;

/**
 * Created by aman on 8/9/17.
 */

public class Friend extends User{
    public String id;
    public String roomNum;
    public String lastMessage;
    public long timeStamp;

    public Friend() {

    }
}
