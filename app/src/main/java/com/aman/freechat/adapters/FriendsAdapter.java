package com.aman.freechat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aman.freechat.R;
import com.aman.freechat.model.Chat;
import com.aman.freechat.model.Friend;
import com.aman.freechat.ui.activities.ChatActivity;
import com.aman.freechat.ui.fragments.FriendChatFragment;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.CircleImageView;
import com.aman.freechat.utils.Constants;

import java.util.List;

/**
 * Created by aman on 15/9/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Friend> friendList;
    private LayoutInflater inflater;
    FriendChatFragment friendChatFragment;

    public void setFragment(FriendChatFragment fragment) {
        this.friendChatFragment = fragment;
    }

    public FriendsAdapter(Context context, List<Friend> friendList) {
        this.context = context;
        this.friendList = friendList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_friend_list, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Friend friend = friendList.get(position);
        FriendsViewHolder viewHolder = ((FriendsViewHolder) holder);

        viewHolder.txt_username.setText(friend.userName);
        if (friend.lastMessage != null) {
            viewHolder.txt_message.setText(friend.lastMessage);
            viewHolder.txt_time.setText(AppUtility.getTimeAgo(friend.timeStamp));
        } else {
            viewHolder.txt_message.setText("No messages");
        }

        viewHolder.card_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(ChatActivity.ROOM_NUM, friend.roomNum);
                intent.putExtra(ChatActivity.FRIEND_NAME, friend.userName);
                intent.putExtra(ChatActivity.FRIEND_ID, friend.id);
                intent.putExtra(ChatActivity.FRIEND_TOKEN, friend.token);
                intent.putExtra(ChatActivity.POS, holder.getAdapterPosition());
                friendChatFragment.startActivityForResult(intent, Constants.REQUEST_CHAT);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (friendList.size() > 0)
            return friendList.size();
        return 0;
    }

    private class FriendsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_pic;
        CardView card_parent;
        TextView txt_username, txt_message, txt_time;

        FriendsViewHolder(View itemView) {
            super(itemView);
            profile_pic = (CircleImageView) itemView.findViewById(R.id.profile_img);
            txt_username = (TextView) itemView.findViewById(R.id.txt_username);
            txt_message = (TextView) itemView.findViewById(R.id.txt_message);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            card_parent = (CardView) itemView.findViewById(R.id.card_parent);
        }
    }
}
