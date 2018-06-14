package com.medicnet.android.photo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medicnet.android.R;
import com.medicnet.android.main.ui.MainActivity;
import com.medicnet.android.photo.adapter.PhotoAdapter;
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

    @BindView(R.id.canvasView)
    CanvasView canvasView;
    @BindView(R.id.recyclerEditPhoto)
    RecyclerView recyclerEditPhoto;
    @BindView(R.id.photoView)
    ImageView imvPhotoView;
    @BindView(R.id.layoutFooter)
    View layoutFooter;
    @BindView(R.id.layoutFooterEdit)
    View layoutFooterEdit;
    @BindView(R.id.btnAction)
    ImageView btnAction;

    @OnClick(R.id.btnDeletePhoto)
    void onPhotoDeleteClicked() {
        if (mainActivity != null) {
            mainActivity.removeBitmap(bitmap, bitmapPosition);
            if (mainActivity.getListBitmap().size() == 0)
                mainActivity.getFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.btnAddPhoto)
    void onPhotoAddClicked() {
        mainActivity.getFragmentManager().popBackStack();
    }

    @OnClick(R.id.btnSendPhoto)
    void onPhotoSendClicked() {
        Uri uri = AppUtil.saveImage(mainActivity, bitmap, 80);
        LogUtil.d(TAG, "onPhotoSendClicked @uri= " + uri.toString());
        mainActivity.getChatRoomFragment().uploadFile(uri);
    }

    @OnClick(R.id.btnCrop)
    void onPhotoCropClicked() {
        handleEditLayout();
        btnAction.setImageResource(R.drawable.ic_photo_crop_rotate);
    }

    @OnClick(R.id.btnBrush)
    void onPhotoBrushClicked() {
        handleEditLayout();
        btnAction.setImageResource(R.drawable.ic_photo_blackbrush);
    }

    @OnClick(R.id.btnCancel)
    void onCancelClicked() {
        layoutFooterEdit.setVisibility(View.GONE);
        canvasView.setVisibility(View.GONE);
        layoutFooter.setVisibility(View.VISIBLE);
        imvPhotoView.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.btnDone)
    void onDoneClicked() {
        onCancelClicked();
        if (mainActivity != null)
            mainActivity.addBitmap(canvasView.getBitmap(), bitmapPosition);
    }

    public static EditPhotoFragment newInstance() {
        EditPhotoFragment fragment = new EditPhotoFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    private MainActivity mainActivity;
    private Bitmap bitmap = null;
    private PhotoAdapter adapter;
    private int bitmapPosition = 0;
    private List<Bitmap> listBitmap;

    private void handleEditLayout() {
        layoutFooterEdit.setVisibility(View.VISIBLE);
        canvasView.setVisibility(View.VISIBLE);
        layoutFooter.setVisibility(View.GONE);
        imvPhotoView.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_photo_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        ButterKnife.bind(this, view);
        listBitmap = mainActivity.getListBitmap();
        setupRecyclerView();
        updatePhotoList(listBitmap);
        if (listBitmap.size() > 0)
            bitmap = listBitmap.get(listBitmap.size() - 1);
        if (bitmap != null)
            initDraw();
        updateBitmapImage(bitmap);
        return view;
    }

    private void setupRecyclerView() {
        LinearLayoutManager horizontalLayoutmanager
                = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        recyclerEditPhoto.setLayoutManager(horizontalLayoutmanager);
    }

    public void updatePhotoList(List<Bitmap> bitmapList) {
        if (adapter == null) {
            adapter = new PhotoAdapter(bitmapList);
            recyclerEditPhoto.setAdapter(adapter);
            adapter.setOnPhotoClickListener(new PhotoAdapter.onPhotoClickListener() {
                @Override
                public void onPhotoClick(int position) {
                    if (position >= 0) {
                        bitmapPosition = position;
                        bitmap = listBitmap.get(position);
                        updateBitmapImage(bitmap);
                    }
                }
            });
        } else
            adapter.notifyDataSetChanged();
    }

    public void updateBitmapImage(Bitmap bitmap) {
        if (bitmap != null) {
            imvPhotoView.setImageBitmap(bitmap);
            canvasView.drawBitmap(bitmap);
        }

    }

    private void initDraw() {
//        canvasView.setMode(CanvasView.Mode.ERASER);
//        canvasView.setDrawer(CanvasView.Drawer.RECTANGLE);
        canvasView.setPaintStyle(Paint.Style.FILL);
        canvasView.setPaintStrokeColor(Color.BLACK);
        canvasView.setBaseColor(Color.BLACK);
    }
}
