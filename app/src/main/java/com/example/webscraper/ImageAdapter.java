package com.example.webscraper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context context;
    private final List<String> imageUrls;
    private final OnDownloadClickListener downloadClickListener;

    public interface OnDownloadClickListener {
        void onDownloadClick(String imageUrl);
    }

    public ImageAdapter(Context context, List<String> imageUrls, OnDownloadClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.downloadClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Use Glide to load the image
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        holder.downloadButton.setOnClickListener(v -> {
            if (downloadClickListener != null) {
                downloadClickListener.onDownloadClick(imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button downloadButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            downloadButton = itemView.findViewById(R.id.download_button);
        }
    }
}