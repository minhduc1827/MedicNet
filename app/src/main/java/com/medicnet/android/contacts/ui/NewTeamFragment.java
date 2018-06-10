package com.medicnet.android.contacts.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.medicnet.android.R;
import com.medicnet.android.contacts.adapter.UserSelectedAdapter;
import com.medicnet.android.contacts.adapter.UsersAdapter;
import com.medicnet.android.contacts.model.UserItem;
import com.medicnet.android.main.ui.MainActivity;
import com.medicnet.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author duc.nguyen
 * @since 6/8/2018
 */
public class NewTeamFragment extends Fragment {

    public static final String TAG = "NewTeamFragment";

    private RecyclerView recyclerViewUser;
    private RecyclerView recyclerViewUserSelected;
    private MainActivity mainActivity;
    private UsersAdapter usersAdapter;
    private UserSelectedAdapter userSelectedAdapter;
    private List<UserItem> listUser = new ArrayList<>();
    private List<String> listUserName = new ArrayList<>();
    private TextView txvUserSelectedEmpty;
    private EditText edtTeamName;
    private View mainView;

    public static NewTeamFragment newInstance() {
        NewTeamFragment fragment = new NewTeamFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_new_team, container, false);
        mainActivity = (MainActivity) getActivity();
        txvUserSelectedEmpty = mainView.findViewById(R.id.txvUserSelectedEmpty);
        edtTeamName = mainView.findViewById(R.id.edtTeamName);
        final android.support.constraint.ConstraintLayout addNewTeam = mainView.findViewById(R.id.layoutAddTeam);
        addNewTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.createNewTeam(edtTeamName.getText().toString(), listUserName, false);
            }
        });
        edtTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == null || editable.toString().equals("")) {
                    LogUtil.d(TAG, "team name is empty so disable onclick add new team");
                    addNewTeam.setClickable(false);
                } else {
                    LogUtil.d(TAG, "team name is not empty");
                    addNewTeam.setClickable(true);
                }
            }
        });
        setupRecyclerViewUser();
        setupRecyclerViewUserSelected();
        mainView.findViewById(R.id.layoutAddTeam);
        return mainView;
    }

    private void setupRecyclerViewUser() {
        recyclerViewUser = mainView.findViewById(R.id.recyclerViewUser);
        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recyclerViewUser.setLayoutManager(verticalLayoutmanager);
        if (mainActivity.getUserList().size() > 0)
            usersAdapter = new UsersAdapter(mainActivity.getUserList());
        usersAdapter.setOnUserListener(new UsersAdapter.OnUserListener() {
            @Override
            public void onUserSelected(boolean isCheck, UserItem user) {
                updateUserSelectedList(isCheck, user);
            }
        });
        recyclerViewUser.setAdapter(usersAdapter);
    }

    private void updateUserSelectedList(boolean isCheck, UserItem user) {
        if (isCheck)
            listUser.add(user);
        else {
            if (listUserName.size() > 0)
                listUserName.clear();
            Iterator<UserItem> iterator = listUser.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(user)) {
                    iterator.remove();
                } else {
                    listUserName.add(iterator.next().username);
                }
            }

        }
        if (listUser.size() > 0)
            txvUserSelectedEmpty.setVisibility(View.GONE);
        else
            txvUserSelectedEmpty.setVisibility(View.GONE);
        if (userSelectedAdapter == null) {
            userSelectedAdapter = new UserSelectedAdapter(listUser);
            recyclerViewUserSelected.setAdapter(userSelectedAdapter);
        } else
            userSelectedAdapter.notifyDataSetChanged();

    }

    private void setupRecyclerViewUserSelected() {
        recyclerViewUserSelected = mainView.findViewById(R.id.recyclerViewUserSeleted);
        LinearLayoutManager horizontalLayoutmanager
                = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewUserSelected.setLayoutManager(horizontalLayoutmanager);

//        recyclerViewUserSelected.setAdapter(usersAdapter);
    }
}
