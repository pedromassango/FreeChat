package com.aman.freechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aman.freechat.R;
import com.aman.freechat.model.Chat;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.CircleImageView;
import com.aman.freechat.utils.SharedPrefHelper;

import java.util.ArrayList;

/**
 * Created by aman on 8/9/17.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int CHAT_MINE = 0;
    private static final int CHAT_OTHER = 1;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Chat> list;

    public ChatAdapter(Context context, ArrayList<Chat> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CHAT_MINE) {
            View view = inflater.inflate(R.layout.item_chat_mine, parent, false);
            return new ChatMineViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_chat_other, parent, false);
        return new ChatOtherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = list.get(position);

        if (holder instanceof ChatMineViewHolder) {
            ChatMineViewHolder viewHolder = ((ChatMineViewHolder) holder);

            viewHolder.name.setText(SharedPrefHelper.getInstance(context).getUserInfo().userName.substring(0, 1).toUpperCase());
            viewHolder.message.setText(chat.message);
            viewHolder.timestamp.setText(AppUtility.getTimeAgo(chat.timeStamp));

            Log.e("ChatHolder", "onBindViewHolder: " + chat.timeStamp);
        } else {
            ChatOtherViewHolder viewHolder = ((ChatOtherViewHolder) holder);

            viewHolder.name.setText(SharedPrefHelper.getInstance(context).getUserInfo().userName.substring(0, 1).toUpperCase());
            viewHolder.message.setText(chat.message);
            viewHolder.timestamp.setText(AppUtility.getTimeAgo(chat.timeStamp));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = list.get(position);
        if (chat.idReceiver.equals(SharedPrefHelper.getInstance(context).getUserInfo().id))
            return CHAT_MINE;
        return CHAT_OTHER;
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    private class ChatMineViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_img;
        TextView name, message, timestamp;

        ChatMineViewHolder(View itemView) {
            super(itemView);
            profile_img = (CircleImageView) itemView.findViewById(R.id.profile_img);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }

    private class ChatOtherViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_img;
        TextView name, message, timestamp;

        ChatOtherViewHolder(View itemView) {
            super(itemView);
            profile_img = (CircleImageView) itemView.findViewById(R.id.profile_img);
            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
