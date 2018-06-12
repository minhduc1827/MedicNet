package com.medicnet.android.photo.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicnet.android.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

/**
 * @author duc.nguyen
 * @since 5/9/2018
 */
public class TakePhotoFragment extends Fragment {

    private CameraView cameraView;
    private View capture;
    private int cameraMethod = CameraKit.Constants.METHOD_STANDARD;
    private boolean cropOutput = false;
    public static final String TAG = "TakePhotoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.take_photo_fragment, container, false);
        cameraView = view.findViewById(R.id.cameraView);
        cameraView.setMethod(cameraMethod);
        cameraView.setCropOutput(cropOutput);
        capture = view.findViewById(R.id.imvCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                    @Override
                    public void callback(CameraKitImage cameraKitImage) {
//                        ((MainActivity)getActivity()).handleTakePhoto(cameraKitImage.getBitmap());
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }
}
