package com.medicnet.android.photo.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medicnet.android.R;

import java.util.List;

/**
 * @author duc.nguyen
 * @since 6/14/2018
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Bitmap> listPhotos;
    private onPhotoClickListener listener;

    public PhotoAdapter(List<Bitmap> listPhotos) {
        this.listPhotos = listPhotos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.imageView.setTag(position);
        holder.imageView.setImageBitmap(listPhotos.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPhotoClick((int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPhotos.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public PhotoViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.photo);
        }
    }

    public void setOnPhotoClickListener(onPhotoClickListener listener) {
        this.listener = listener;
    }

    public interface onPhotoClickListener {
        void onPhotoClick(int position);
    }
}
