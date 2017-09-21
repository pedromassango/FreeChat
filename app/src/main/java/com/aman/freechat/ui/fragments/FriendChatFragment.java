package com.aman.freechat.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.aman.freechat.R;
import com.aman.freechat.adapters.FriendsAdapter;
import com.aman.freechat.db.ChatDB;
import com.aman.freechat.db.DBHelper;
import com.aman.freechat.db.FriendDB;
import com.aman.freechat.model.Chat;
import com.aman.freechat.model.Friend;
import com.aman.freechat.model.User;
import com.aman.freechat.ui.activities.ChatActivity;
import com.aman.freechat.utils.AppUtility;
import com.aman.freechat.utils.Constants;
import com.aman.freechat.utils.SharedPrefHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aman on 6/9/17.
 */

public class FriendChatFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = FriendChatFragment.class.getName();
    private User currentUser;
    private RecyclerView recycler_friends;
    private FloatingActionButton add_friend;
    private List<Friend> friendsList;
    private FriendsAdapter friendsAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<String> friendIdlist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = SharedPrefHelper.getInstance(getActivity()).getUserInfo();

        if (!AppUtility.isNetworkAvailable(getActivity())) {
            friendsList = FriendDB.getInstance().getAllFriends();
        } else {
            friendsList = new ArrayList<>();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (friendsList == null || friendsList.size() == 0) {
            AppUtility.showDialog(getActivity());
            getAllFriends(SharedPrefHelper.getInstance(getActivity()).getUserInfo().id,
                    AppUtility.isNetworkAvailable(getActivity()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_chat, null);
        initView(view);

        return view;
    }

    private void getAllFriends(String id, final boolean fromLocal) {
        databaseReference.child("friend/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                    for (DataSnapshot snapshot : snapshots) {
                        String friendId = snapshot.getValue().toString();
                        friendIdlist.add(friendId);
                    }

                    getFriend(0, fromLocal);
                } else {
                    AppUtility.dismissDialog();
                    Toast.makeText(getActivity(), "No friends, start adding friends and chat with them now!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFriend(final int index, final boolean addToLocal) {
        if (index == friendIdlist.size()) {
            friendsAdapter.notifyDataSetChanged();
            AppUtility.dismissDialog();
            if (addToLocal && FriendDB.getInstance().getAllFriends().size() != friendsList.size())
                FriendDB.getInstance().addFriendList(friendsList);
        } else {
            final String id = friendIdlist.get(index);

            databaseReference.child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        final Friend friend = new Friend();
                        friend.id = dataSnapshot.getValue(User.class).id;
                        friend.userName = dataSnapshot.getValue(User.class).userName;
                        friend.email = dataSnapshot.getValue(User.class).email;
                        friend.image = dataSnapshot.getValue(User.class).image;
                        friend.roomNum = id.compareTo(currentUser.id) > 0 ? String.valueOf((currentUser.id + id).hashCode()) : String.valueOf((id + currentUser.id).hashCode());
                        friend.token = dataSnapshot.getValue(User.class).token;

                        Chat chat = ChatDB.getInstance().getLastMessage(friend.roomNum);

                        if (chat != null) {
                            friend.lastMessage = chat.message;
                            friend.timeStamp = chat.timeStamp;
                        } else {
                            databaseReference.child("message/" + friend.roomNum).limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Chat chat = dataSnapshot.getValue(Chat.class);
                                    friend.lastMessage = chat.message;
                                    friend.timeStamp = chat.timeStamp;
                                    friendsAdapter.notifyDataSetChanged();
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

                        friendsList.add(friend);
                    }

                    getFriend(index + 1, addToLocal);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void initView(View view) {
        recycler_friends = (RecyclerView) view.findViewById(R.id.recycler_friends);
        recycler_friends.setLayoutManager(new LinearLayoutManager(getActivity()));
        add_friend = (FloatingActionButton) view.findViewById(R.id.add_friend);

        recycler_friends.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    add_friend.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && add_friend.isShown()) {
                    add_friend.hide();
                }
            }
        });

        add_friend.setOnClickListener(this);

        if (friendsList != null) {
            friendsAdapter = new FriendsAdapter(getActivity(), friendsList);
            friendsAdapter.setFragment(this);
            recycler_friends.setAdapter(friendsAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_friend:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(15);
        drawable.setStroke(1, Color.parseColor("#AAAAAA"));
        drawable.setColor(Color.parseColor("#FFFFFF"));

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final CardView d = (CardView) dialog.findViewById(R.id.cardView);
        d.setBackgroundColor(Color.parseColor("#DADADA"));

        final TextInputEditText friend_email = (TextInputEditText) dialog.findViewById(R.id.friend_email);
        Button submit = (Button) dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(friend_email.getText())) {
                    friend_email.setError("Please enter an email address");
                } else if (AppUtility.validate(friend_email) == Constants.INVALID_EMAIL) {
                    friend_email.setError("Please enter a valid email address");
                } else {
                    AppUtility.showDialog(getActivity());
                    addFriend(friend_email.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        d.setBackground(drawable);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void addFriend(final String email) {
        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppUtility.dismissDialog();
                if (dataSnapshot.getValue() == null) {
                    AppUtility.showAlertDialog(getActivity(), "Error", "Email not found", null);
                } else {
                    String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                    if (id.equals(currentUser.id)) {
                        AppUtility.showAlertDialog(getActivity(), "Error", "Please enter a different email address", null);
                    } else {
                        HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                        Friend friend = new Friend();
                        friend.image = "";
                        friend.userName = (String) userMap.get("userName");
                        friend.email = (String) userMap.get("email");
                        friend.id = id;
                        friend.roomNum = id.compareTo(currentUser.id) > 0 ? String.valueOf((id + currentUser.id).hashCode()) : String.valueOf((currentUser.id + id).hashCode());

                        AppUtility.showDialog(getActivity());

                        checkIfAlreadyFriend(id, friend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfAlreadyFriend(final String id, final Friend friend) {
        FirebaseDatabase.getInstance().getReference().child("friend/" + currentUser.id).orderByValue().equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            AppUtility.dismissDialog();
                            AppUtility.showAlertDialog(getActivity(), "Already Friend", friend.email + " is already in your friend list.", null);
                        } else {
                            checkAndAdd(friend, true);
                            addToLocalRefresh(friend);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToLocalRefresh(Friend friend) {
        if (friendsAdapter != null) {
            if (FriendDB.getInstance().addFriend(friend)) {
                friendsList.add(friend);
                friendsAdapter.notifyItemInserted(friendsList.size() - 1);
            }
        } else {
            FriendDB.getInstance().addFriend(friend);
            friendsList = new ArrayList<>();
            friendsList.add(friend);
            friendsAdapter = new FriendsAdapter(getActivity(), friendsList);
            recycler_friends.setAdapter(friendsAdapter);
        }
    }

    private void checkAndAdd(final Friend friend, boolean isAdded) {
        if (friend != null) {
            if (isAdded) {
                FirebaseDatabase.getInstance().getReference().child("friend/" + currentUser.id).push().setValue(friend.id)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkAndAdd(friend, false);
                                } else {
                                    AppUtility.dismissDialog();
                                }
                            }
                        });
            } else {
                FirebaseDatabase.getInstance().getReference().child("friend/" + friend.id).push().setValue(currentUser.id)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkAndAdd(null, false);
                                } else {
                                    AppUtility.dismissDialog();
                                }
                            }
                        });
            }
        } else {
            AppUtility.dismissDialog();
            AppUtility.showAlertDialog(getActivity(), "Success", "Friend added successfully.", null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CHAT) {

            if (data != null) {
                int position_clicked = data.getIntExtra(ChatActivity.POS, -1);
                if (position_clicked != -1) {
                    friendsList.get(position_clicked).lastMessage = data.getStringExtra(ChatActivity.CHAT);
                    friendsList.get(position_clicked).timeStamp = data.getLongExtra(ChatActivity.TIMESTAMP, 0);
                    friendsAdapter.notifyItemChanged(position_clicked);
                }
            }
        }
    }
}
