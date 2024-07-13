package com.example.edudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {
    private Context context;
    private List<ImageModel> imageList;
    private ProgressBar progressBar;

    public ImageSliderAdapter(Context context, List<ImageModel> imageList, ProgressBar progressBar) {
        this.context = context;
        this.imageList = imageList;
        this.progressBar = progressBar;
    }

    @Override
    public ImageSliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.image_slider_item, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(ImageSliderAdapter.SliderAdapterVH viewHolder, int position) {
        ImageModel imageModel = imageList.get(position);
//        Picasso.get().load(imageModel.getImageUrl()).into(viewHolder.imageView);
        Glide.with(viewHolder.itemView)
                .load(imageModel.getImageUrl())
                .into(viewHolder.imageView);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public int getCount() {
        return imageList.size();


    }


    public class SliderAdapterVH extends ViewHolder {
        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
