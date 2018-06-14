package com.medicnet.android.photo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.medicnet.android.R;
import com.medicnet.android.main.ui.MainActivity;
import com.medicnet.android.photo.adapter.PhotoAdapter;
import com.medicnet.android.util.LogUtil;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * @author duc.nguyen
 * @since 6/12/2018
 */
public class TakePhotoFragment extends Fragment {

    public static final String TAG = "TakePhotoFragment";

    @BindView(R.id.cameraView)
    CameraView cameraView;
    @BindView(R.id.facingButton)
    ImageView facingButton;
    @BindView(R.id.flashButton)
    ImageView flashButton;
    @BindView(R.id.blackCover)
    View coverView;
    @BindView(R.id.recyclerViewPhoto)
    RecyclerView recyclerView;

    @OnClick(R.id.imvCapture)
    void onCaptureClicked() {
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
//                        ((MainActivity)getActivity()).handleTakePhoto(cameraKitImage.getBitmap());
                mainActivity.presenter.toEditPhoto();
                mainActivity.addBitmap(cameraKitImage.getBitmap(), -1);
                LogUtil.d(TAG, "captureImage callback>>" + cameraKitImage.getMessage());
            }
        });
    }


    private int cameraMethod = CameraKit.Constants.METHOD_STANDARD;
    private boolean cropOutput = false;
    private MainActivity mainActivity;
    private PhotoAdapter adapter;

    public static TakePhotoFragment newInstance() {
        TakePhotoFragment fragment = new TakePhotoFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.take_photo_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        ButterKnife.bind(this, view);
//        ButterKnife.setDebug(true);
        setupRecyclerView();
        cameraView.setMethod(cameraMethod);
        cameraView.setCropOutput(cropOutput);
        setFacingImageBasedOnCamera();
        return view;
    }

    private void setupRecyclerView() {
        LinearLayoutManager horizontalLayoutmanager
                = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutmanager);
    }

    public void updatePhotoList(List<Bitmap> bitmapList) {
        if (adapter == null) {
            adapter = new PhotoAdapter(bitmapList);
            recyclerView.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    private void setFacingImageBasedOnCamera() {
        if (cameraView.isFacingFront()) {
            facingButton.setImageResource(R.drawable.ic_facing_back);
        } else {
            facingButton.setImageResource(R.drawable.ic_facing_front);
        }
    }


    @OnTouch(R.id.facingButton)
    boolean onTouchFacing(final View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                coverView.setAlpha(0);
                coverView.setVisibility(View.VISIBLE);
                coverView.animate()
                        .alpha(1)
                        .setStartDelay(0)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (cameraView.isFacingFront()) {
                                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                                    changeViewImageResource((ImageView) view, R.drawable
                                            .ic_facing_front);
                                    flashButton.setVisibility(View.VISIBLE);
                                } else {
                                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                                    changeViewImageResource((ImageView) view, R.drawable
                                            .ic_facing_back);
                                    flashButton.setVisibility(View.GONE);
                                }

                                coverView.animate()
                                        .alpha(0)
                                        .setStartDelay(200)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                coverView.setVisibility(View.GONE);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();

                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flashButton)
    boolean onTouchFlash(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == CameraKit.Constants.FLASH_AUTO) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    setResourceId((ImageView) view, R.drawable.ic_camera_flash_on);
                } else if (cameraView.getFlash() == CameraKit.Constants.FLASH_ON) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    setResourceId((ImageView) view, R.drawable.ic_camera_flash_off);
                } else {
                    cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
                    setResourceId((ImageView) view, R.drawable.ic_camera_flash_auto);
                }

                break;
            }
        }
        return true;
    }

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    boolean handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setResourceId(imageView, resId);
            }
        }, 120);
    }

    private void setResourceId(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setImageResource(resId);
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
