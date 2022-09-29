package com.joek.nuxtube.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.joek.nuxtube.R;
import com.joek.nuxtube.data.model.related.RelatedItem;
import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.listeners.RelatedClickListener;
import com.joek.nuxtube.listeners.SearchClickListener;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    ArrayList<RelatedItem> relatedItems;
    Context context;
    RelatedClickListener.OnItemClickListener onItemClickListener;

    public PlayerAdapter(ArrayList<RelatedItem> relatedItems, Context context, RelatedClickListener.OnItemClickListener onItemClickListener) {
        this.relatedItems = relatedItems;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_search,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setOnClickListener(view -> {
            onItemClickListener.onItemClick(relatedItems.get(position));
        });
        Glide.with(context).load(relatedItems.get(position).getThumbnail())
                .placeholder(new ColorDrawable(Color.GRAY))
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imageView);
        Glide.with(context).load(relatedItems.get(position).getAvatarUrl())
                .placeholder(new ColorDrawable(Color.GRAY))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.avatar);
        holder.textTitle.setText(relatedItems.get(position).getTitle());
        if (relatedItems.get(position).getLive()) {
            holder.textLive.setVisibility(View.VISIBLE);
            holder.textDuration.setVisibility(View.GONE);
            holder.textSecondary.setText(relatedItems.get(position).getChannelName() + " . " + relatedItems.get(position).getViewCounts());
        }else{
            String secondary = relatedItems.get(position).getChannelName() + " . " +  relatedItems.get(position).getViewCounts() + " . " + relatedItems.get(position).getPublishedAt();
            holder.textSecondary.setText(secondary);
            holder.textDuration.setText(relatedItems.get(position).getLengthText());
            holder.textDuration.setVisibility(View.VISIBLE);
            holder.textLive.setVisibility(View.GONE);
        }
        holder.linearChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return relatedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView cardView;
        ImageView imageView, avatar;
        TextView textTitle, textSecondary, textDuration, textLive;
        LinearLayout linearChannel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_search);
            imageView = itemView.findViewById(R.id.imageView);
            textTitle = itemView.findViewById(R.id.title);
            textSecondary = itemView.findViewById(R.id.secondary);
            textLive = itemView.findViewById(R.id.text_live);
            avatar = itemView.findViewById(R.id.avatar_image);
            textDuration = itemView.findViewById(R.id.text_duration);
            linearChannel = itemView.findViewById(R.id.panel_channel);
        }
    }
}
