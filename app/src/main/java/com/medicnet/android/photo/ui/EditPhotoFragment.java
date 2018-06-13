package com.medicnet.android.photo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicnet.android.R;
import com.medicnet.android.main.ui.MainActivity;
import com.medicnet.android.photo.presentation.CanvasView;
import com.medicnet.android.util.AppUtil;
import com.medicnet.android.util.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author duc.nguyen
 * @since 6/13/2018
 */
public class EditPhotoFragment extends Fragment {

    public static final String TAG = "EditPhotoFragment";
    private MainActivity mainActivity;
    private Bitmap bitmap = null;
    @BindView(R.id.canvasView)
    CanvasView canvasView;
    private List<Bitmap> listBitmap;

    @OnClick(R.id.btnDeletePhoto)
    void onPhotoDeleteClicked() {

    }

    @OnClick(R.id.btnAddPhoto)
    void onPhotoAddClicked() {

    }

    @OnClick(R.id.btnSendPhoto)
    void onPhotoSendClicked() {
        Uri uri = AppUtil.saveImage(mainActivity, bitmap, 80);
        LogUtil.d(TAG, "onPhotoSendClicked @uri= " + uri.toString());
        mainActivity.getChatRoomFragment().uploadFile(uri);
    }

    @OnClick(R.id.btnCrop)
    void onPhotoCropClicked() {

    }

    @OnClick(R.id.btnBrush)
    void onPhotoBrushClicked() {

    }

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
        mainActivity = (MainActivity) getActivity();
        ButterKnife.bind(this, view);
        listBitmap = mainActivity.getListBitmap();

        if (listBitmap.size() > 0)
            bitmap = listBitmap.get(listBitmap.size() - 1);
        if (bitmap != null)
            initDraw();
        updateBitmapImage(bitmap);
        return view;
    }

    public void updateBitmapImage(Bitmap bitmap) {
        if (bitmap != null)
            canvasView.drawBitmap(bitmap);

    }

    private void initDraw() {
//        canvasView.setMode(CanvasView.Mode.ERASER);
        canvasView.setDrawer(CanvasView.Drawer.RECTANGLE);
        canvasView.setPaintStyle(Paint.Style.FILL);
        canvasView.setPaintStrokeColor(Color.WHITE);
        canvasView.setBaseColor(Color.WHITE);
    }
}
