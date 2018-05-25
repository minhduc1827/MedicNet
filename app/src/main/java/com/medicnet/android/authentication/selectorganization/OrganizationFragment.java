package com.medicnet.android.authentication.selectorganization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.medicnet.android.R;
import com.medicnet.android.authentication.domain.model.OrganizationElement;
import com.medicnet.android.authentication.domain.model.Organizations;
import com.medicnet.android.authentication.ui.AuthenticationActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

public class OrganizationFragment extends Fragment {

    private static final String JSON_DATA = "json_data";
    private final String TAG = getClass().getSimpleName();
    private ListView lvOrganization;
    private ArrayAdapter<String> adapter;
    private List<String> organizations = new ArrayList<>();

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
        lvOrganization = view.findViewById(R.id.lvOrganization);
        setupListView();
        lvOrganization.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((AuthenticationActivity) getActivity()).setOrganzation(organizations.get
                        (position));
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }

    protected void setupListView() {
        String jsonData = getArguments().getString(JSON_DATA);
        Log.d(TAG, "json data>> " + jsonData);
        if (!jsonData.isEmpty()) {
            Moshi moshi = new Moshi.Builder().build();
//            Type type= Types.newParameterizedType(List.class, Organization.class);
//            JsonAdapter<List<Organization>>adapter=moshi.adapter(type);
            JsonAdapter<Organizations> jsonAdapter = moshi.adapter(Organizations.class);
            try {
                Organizations organizationList = jsonAdapter.fromJson(jsonData);

                for (OrganizationElement organization : organizationList.organizations) {
                    organizations.add(organization.value);
                }
                if (organizations.size() > 0) {
                    adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_organization,
                            organizations);
                    lvOrganization.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
