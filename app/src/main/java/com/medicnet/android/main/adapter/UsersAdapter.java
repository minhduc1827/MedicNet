package com.medicnet.android.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.medicnet.android.R;
import com.medicnet.android.main.user.model.UserItem;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<UserItem> listUsers;
    private final String avatarUrl = "https://medicappdev.eastus.cloudapp.azure.com/avatar/%s?format=jpeg";

    public UsersAdapter(List<UserItem> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = listUsers.get(position);
        holder.imvAvatar.setImageURI(String.format(avatarUrl, user.username));
        holder.txvUserName.setText(user.username);
        holder.txvUserStatus.setText(getUserStatus(user.status));
    }

    private String getUserStatus(String status) {
        if (status.equals("online"))
            return "Available";
        else
            return "On Leave";
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView txvUserName, txvUserStatus;
        public SimpleDraweeView imvAvatar;

        public UserViewHolder(View view) {
            super(view);
            txvUserName = view.findViewById(R.id.txvUserName);
            txvUserStatus = view.findViewById(R.id.txvUserStatus);
            imvAvatar = view.findViewById(R.id.imvAvatar);
        }
    }
}
