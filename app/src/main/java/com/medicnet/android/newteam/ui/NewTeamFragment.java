package com.medicnet.android.newteam.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicnet.android.R;
import com.medicnet.android.main.adapter.UsersAdapter;
import com.medicnet.android.main.ui.MainActivity;

/**
 * @author duc.nguyen
 * @since 6/8/2018
 */
public class NewTeamFragment extends Fragment {

    private RecyclerView recyclerViewUser;
    private MainActivity mainActivity;
    private UsersAdapter usersAdapter;
    public static final String TAG = "NewTeamFragment";

    public static NewTeamFragment newInstance() {
        NewTeamFragment fragment = new NewTeamFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_team, container, false);
        recyclerViewUser = view.findViewById(R.id.recyclerViewUser);
        mainActivity = (MainActivity) getActivity();
        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recyclerViewUser.setLayoutManager(verticalLayoutmanager);
        if (mainActivity.getUserList().size() > 0)
            usersAdapter = new UsersAdapter(mainActivity.getUserList());
        recyclerViewUser.setAdapter(usersAdapter);
        return view;
    }
}
