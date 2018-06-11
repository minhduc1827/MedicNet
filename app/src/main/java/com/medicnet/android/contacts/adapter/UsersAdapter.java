package com.medicnet.android.contacts.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private boolean isCheckboxTouch = false;

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
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final UserItem user = listUsers.get(position);
        LogUtil.d(TAG, "onBindViewHolder @username= " + user.username + " @isChecked=" + user.selected);
        holder.imvAvatar.setImageURI(String.format(avatarUrl, user.username));
        holder.txvUserName.setText(user.username);
        holder.txvUserStatus.setText(getUserStatus(user.status));
        holder.cbUserSelect.setChecked(user.selected);
        holder.cbUserSelect.setTag(user);
        holder.cbUserSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // touch down code
                        LogUtil.d(TAG, "ontouch down checkbox user");
                        isCheckboxTouch = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // touch move code
                        break;

                    case MotionEvent.ACTION_UP:
                        // touch up code
                        break;
                }
                return false;
            }
        });
        holder.cbUserSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isCheckboxTouch) {
                    isCheckboxTouch = false;
                    LogUtil.d(TAG, "oncheck change by touch");
                    setItemSelect(isChecked, holder.cbUserSelect);
                    if (listener != null)
                        listener.onUserSelected(isChecked, (UserItem) compoundButton.getTag());
                }
            }
        });
    }

    private void setItemSelect(boolean isChecked, CheckBox checkBox) {
        UserItem userItem = (UserItem) checkBox.getTag();
        for (int i = 0; i < listUsersOrginal.size(); i++) {
            UserItem item = listUsersOrginal.get(i);
            LogUtil.d(TAG, "setItemSelect @username= " + userItem.username + " item @username= " +
                    "" + item.username);
            if (item.username.equals(userItem.username)) {
                item.selected = isChecked;
                checkBox.setTag(item);
//                listUsers.set(i,item);
                listUsersOrginal.set(i, item);
            }
        }
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
            LogUtil.d(TAG, "filter with empty text");
        } else {
            text = text.toLowerCase();
            LogUtil.d(TAG, "filter with no empty text");
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
