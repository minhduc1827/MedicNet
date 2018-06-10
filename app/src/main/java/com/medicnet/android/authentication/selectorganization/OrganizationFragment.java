package com.medicnet.android.authentication.selectorganization;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.medicnet.android.R;
import com.medicnet.android.authentication.organization.model.OrganizationItem;
import com.medicnet.android.authentication.organization.model.Organizations;
import com.medicnet.android.authentication.ui.AuthenticationActivity;
import com.medicnet.android.util.LogUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

public class OrganizationFragment extends Fragment {

    public static final String JSON_DATA = "json_data";
    protected final String TAG = getClass().getSimpleName();
    protected ListView listView;
    protected ArrayAdapter<String> adapter;
    protected List<String> dataList = new ArrayList<>();
    private View mainView;

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

    private void setupSearchView() {
        android.support.v7.widget.SearchView searchView = mainView.findViewById(R.id.searchView);
        ImageView iconSearch = searchView.findViewById(android.support.v7.appcompat.R.id
                .search_button);
        iconSearch.setColorFilter(Color.BLACK);
        ImageView iconClear = searchView.findViewById(android.support.v7.appcompat.R.id
                .search_close_btn);
        iconClear.setColorFilter(Color.BLACK);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView
                .OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat
                .R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
    }

    private void setupListView() {
        listView = mainView.findViewById(R.id.lvOrganization);
        if (dataList.size() > 0) {
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_organization,
                    dataList);
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateDataForResgister(position);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_select_organization, container, false);
        setupSearchView();
        setupListViewAdapter();
        setupListView();
        return mainView;
    }

    protected void updateDataForResgister(int position){
        ((AuthenticationActivity) getActivity()).setOrganzation(adapter.getItem
                (position));
    }

    protected void setupListViewAdapter() {
        String jsonData = getArguments().getString(JSON_DATA);
        LogUtil.d(TAG, "json data>> " + jsonData);
        if (!jsonData.isEmpty()) {
            Moshi moshi = new Moshi.Builder().build();
//            Type type= Types.newParameterizedType(List.class, Organization.class);
//            JsonAdapter<List<Organization>>adapter=moshi.adapter(type);
            JsonAdapter<Organizations> jsonAdapter = moshi.adapter(Organizations.class);
            try {
                Organizations organizationList = jsonAdapter.fromJson(jsonData);
                for (OrganizationItem organization : organizationList.organizations) {
                    dataList.add(organization.value);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
