package com.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.activities.ChatActivity;
import com.app.model.User;
import com.bumptech.glide.Glide;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends UltimateViewAdapter<UserAdapter.CustomViewHolder> {
    ArrayList<User> listUsers;
    Context context;

    public UserAdapter(ArrayList<User> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }

    @Override
    public CustomViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public CustomViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public int getAdapterItemCount() {
        return listUsers.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        User user = listUsers.get(position);
        Glide.with(context).load(user.getAvt()).into(holder.avtUser);
        holder.userName.setText(user.getName());
        holder.lastMessage.setText(user.getLastMessage());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avtUser;
        private TextView userName, lastMessage;

        public CustomViewHolder(View itemView) {
            super(itemView);
            avtUser = itemView.findViewById(R.id.avatar_user_chat);
            userName = itemView.findViewById(R.id.user_name_chat);
            lastMessage = itemView.findViewById(R.id.last_message);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                }
            });
        }
    }
}
