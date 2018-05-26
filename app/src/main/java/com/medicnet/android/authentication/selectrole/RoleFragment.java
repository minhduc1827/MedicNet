package com.medicnet.android.authentication.selectrole;

import android.os.Bundle;

import com.medicnet.android.authentication.selectorganization.OrganizationFragment;
import com.medicnet.android.authentication.ui.AuthenticationActivity;

/**
 * @author duc.nguyen
 * @since 5/25/2018
 */
public class RoleFragment extends OrganizationFragment {
    @Override
    protected void setupListViewAdapter() {
        dataList.add("Consultant");
        dataList.add("Specialist Registrar");
        dataList.add("Specialty Doctor");
        dataList.add("Core trainee");
        dataList.add("Foundation trainee year 1");
        dataList.add("Foundation trainee year 2");
        dataList.add("Medical student");
        dataList.add("Other – specify");
        dataList.add("Staff nurse");
        dataList.add("Senior staff nurse");
        dataList.add("Sister");
        dataList.add("Matron");
    }

    public static RoleFragment newInstance(String json) {
        Bundle args = new Bundle();
        args.putString(JSON_DATA, json);
        RoleFragment fragment = new RoleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void updateDataForResgister(int position) {
        ((AuthenticationActivity) getActivity()).setRole(adapter.getItem
                (position));
    }
}
