package com.medicnet.android.authentication.selectorganization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicnet.android.R;

public class OrganizationFragment extends Fragment {

    private static final String JSON_DATA = "json_data";
    private final String TAG = getClass().getSimpleName();

    public static OrganizationFragment newInstance(String json) {

        Bundle args = new Bundle();
        args.putString(JSON_DATA, json);
        OrganizationFragment fragment = new OrganizationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AndroidSupportInjection.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_organization, container, false);
        Log.d(TAG, "json data>> " + getArguments().getString(JSON_DATA));
        return view;
    }
}
