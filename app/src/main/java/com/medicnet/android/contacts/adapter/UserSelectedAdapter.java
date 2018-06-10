package com.medicnet.android.contacts.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.medicnet.android.R;
import com.medicnet.android.contacts.model.UserItem;

import java.util.List;

public class UserSelectedAdapter extends RecyclerView.Adapter<UserSelectedAdapter.UserViewHolder> {

    private List<UserItem> listUsers;
    private final String avatarUrl = "https://medicappdev.eastus.cloudapp.azure.com/avatar/%s?format=jpeg";

    public UserSelectedAdapter(List<UserItem> listUsers) {
        this.listUsers = listUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_selected, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserItem user = listUsers.get(position);
        holder.imvAvatar.setImageURI(String.format(avatarUrl, user.username));
        holder.txvUserName.setText(user.username);
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView txvUserName;
        public SimpleDraweeView imvAvatar;

        public UserViewHolder(View view) {
            super(view);
            txvUserName = view.findViewById(R.id.txvUserName);
            imvAvatar = view.findViewById(R.id.imvAvatar);
        }
    }
}
