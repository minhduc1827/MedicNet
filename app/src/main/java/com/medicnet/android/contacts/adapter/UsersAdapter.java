package com.medicnet.android.contacts.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.medicnet.android.R;
import com.medicnet.android.contacts.model.UserItem;
import com.medicnet.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<UserItem> listUsers;
    private List<UserItem> listUsersOrginal = new ArrayList<>();
    private final String avatarUrl = "https://medicappdev.eastus.cloudapp.azure.com/avatar/%s?format=jpeg";
    private OnUserListener listener;
    private final String TAG = UsersAdapter.class.getSimpleName();

    public UsersAdapter(List<UserItem> listUsers) {
        this.listUsers = listUsers;
        listUsersOrginal.addAll(listUsers);
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
        holder.cbUserSelect.setChecked(false);
        UserItem userItem = (UserItem) holder.cbUserSelect.getTag();
        if (userItem != null) {
            LogUtil.d(TAG, "userItem checked @username= " + userItem.username);
            if (userItem.username.equals(user.username))
                holder.cbUserSelect.setChecked(true);
        } else
            holder.cbUserSelect.setTag(user);
        holder.cbUserSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (listener != null)
                    listener.onUserSelected(isChecked, (UserItem) compoundButton.getTag());
            }
        });
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
        public CheckBox cbUserSelect;

        public UserViewHolder(View view) {
            super(view);
            txvUserName = view.findViewById(R.id.txvUserName);
            txvUserStatus = view.findViewById(R.id.txvUserStatus);
            imvAvatar = view.findViewById(R.id.imvAvatar);
            cbUserSelect = view.findViewById(R.id.cbUserSelect);
        }
    }

    public void setOnUserListener(OnUserListener listener) {
        this.listener = listener;
    }

    public void filter(String text) {
        listUsers.clear();
        if (text.isEmpty()) {
            listUsers.addAll(listUsersOrginal);
        } else {
            text = text.toLowerCase();
            for (UserItem item : listUsersOrginal) {
                if (item.username.toLowerCase().contains(text) || item.name.toLowerCase().contains(text)) {
                    listUsers.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnUserListener {
        void onUserSelected(boolean isCheck, UserItem user);
    }
}
