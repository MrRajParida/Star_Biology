package com.example.demo;

import static android.media.CamcorderProfile.get;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<itemClass> itemClassList;
    private List<itemClass> itemClassFullList;
    private Context context;
    private OnItemClickListener listener;

    public Adapter(List<itemClass> itemClassList, Context context, OnItemClickListener listener) {
        this.itemClassList = itemClassList;
        this.context = context;
        this.listener = listener;

        this.itemClassFullList = new ArrayList<>(itemClassList);
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        itemClass itemClass = itemClassList.get(position);

        holder.textName.setText(itemClass.getName());
        holder.textEmail.setText(itemClass.getEmail());
        holder.imageView.setImageResource(itemClass.getImg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(itemClass);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemClassList.size();
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(itemClassList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName, textEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
        }
    }
    public void filter(String newText) {
        itemClassList.clear();

        if (newText.isEmpty()) {
            itemClassList.addAll(itemClassFullList);
        } else {
            newText = newText.toLowerCase();

            for (itemClass item : itemClassFullList) {

                if (item.getName().toLowerCase().contains(newText) || item.getEmail().toLowerCase().contains(newText)) {
                    itemClassList.add(item);
                }

            }
        }
        notifyDataSetChanged();
    }
}
