package com.medicnet.android.photo.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicnet.android.R;

/**
 * @author duc.nguyen
 * @since 6/13/2018
 */
public class EditPhotoFragment extends Fragment {

    public static final String TAG = "EditPhotoFragment";

    public static EditPhotoFragment newInstance() {
        EditPhotoFragment fragment = new EditPhotoFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_photo_fragment, container, false);
        return view;
    }
}
