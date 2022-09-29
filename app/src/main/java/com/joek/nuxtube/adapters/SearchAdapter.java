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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.joek.nuxtube.R;
import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.listeners.SearchClickListener;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    FragmentManager fragmentManager;
    ArrayList<SearchItem> searchItems;
    Context context;
    SearchClickListener.OnItemClickListener onItemClickListener;
    public SearchAdapter(FragmentManager childFragmentManager, ArrayList<SearchItem> searchItems, Context context, SearchClickListener.OnItemClickListener onItemClickListener) {
        fragmentManager = childFragmentManager;
        this.searchItems = searchItems;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_search,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder viewHolder, int i) {
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.imageView.setOnClickListener(view -> {
            onItemClickListener.onItemClick(searchItems.get(i));
        });
        Glide.with(context).load(searchItems.get(i).getImageUrl())
                .placeholder(new ColorDrawable(Color.GRAY))
                .transition(withCrossFade())
                .into(viewHolder.imageView);
        Glide.with(context).load(searchItems.get(i).getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.avatar);
        viewHolder.textTitle.setText(searchItems.get(i).getTitle());
        if (searchItems.get(i).getLive()) {
            viewHolder.textLive.setVisibility(View.VISIBLE);
            viewHolder.textDuration.setVisibility(View.GONE);
            viewHolder.textSecondary.setText(searchItems.get(i).getChannelText() + " . " + searchItems.get(i).getViewCounts());
        }else{
            String secondary = searchItems.get(i).getChannelText() + " . " +  searchItems.get(i).getViewCounts() + " . " + searchItems.get(i).getPublishedAt();
            viewHolder.textSecondary.setText(secondary);
            viewHolder.textDuration.setText(searchItems.get(i).getLengthText());
            viewHolder.textDuration.setVisibility(View.VISIBLE);
            viewHolder.textLive.setVisibility(View.GONE);
        }
        //viewHolder.textSupporting.setText(searchItems.get(i).getSupportingText());
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView cardView;
        ImageView imageView, avatar;
        TextView textTitle, textSecondary, textLive, textDuration;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_search);
            imageView = itemView.findViewById(R.id.imageView);
            textTitle = itemView.findViewById(R.id.title);
            textSecondary = itemView.findViewById(R.id.secondary);
            avatar = itemView.findViewById(R.id.avatar_image);
            textLive = itemView.findViewById(R.id.text_live);
            linearLayout = itemView.findViewById(R.id.panel_channel);
            textDuration = itemView.findViewById(R.id.text_duration);
        }
    }
}
