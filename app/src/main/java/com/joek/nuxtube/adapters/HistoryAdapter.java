package com.joek.nuxtube.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.joek.nuxtube.R;
import com.joek.nuxtube.data.model.history.History;
import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.listeners.SearchClickListener;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    FragmentManager fragmentManager;
    ArrayList<History> historyArrayList;
    Context context;
    Boolean showCheckBoxes = false;
    private SearchClickListener.OnItemClickListener onItemClickListener;
    MutableLiveData<Boolean> liveDataIsSelectedAll = new MutableLiveData<>();
    MutableLiveData<Pair<Integer, Boolean>> selectedItems = new MutableLiveData<>();
    private int lastPosition = -1;
    private boolean isChecked = false;

    public HistoryAdapter(FragmentManager childFragmentManager, ArrayList<History> historyArrayList, Context context, SearchClickListener.OnItemClickListener onItemClickListener) {
        fragmentManager = childFragmentManager;
        this.historyArrayList = historyArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_history,viewGroup,false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.cardView.setOnClickListener(view -> {
            onItemClickListener.onItemClick(new SearchItem(historyArrayList.get(i).getVideoId(),
                    historyArrayList.get(i).getThumbnail(),
                    historyArrayList.get(i).getTitle(),
                    historyArrayList.get(i).getViewCounts(),
                    historyArrayList.get(i).getPublishedAt(),
                    historyArrayList.get(i).getDuration(),
                    historyArrayList.get(i).getChannelName(),
                    "",
                    historyArrayList.get(i).getChannelThumbnail(),
                    historyArrayList.get(i).getIsLive()));
        });

        viewHolder.checkBox.setVisibility(showCheckBoxes ? View.VISIBLE : View.GONE);

        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            selectedItems.setValue(new Pair<>(i, b));
        });
        viewHolder.cardView.setOnLongClickListener(view -> {
            showCheckBoxes = true;
            viewHolder.checkBox.setChecked(true);
            liveDataIsSelectedAll.setValue(true);
            notifyDataSetChanged();
            return true;
        });
        Glide.with(context).load(historyArrayList.get(i).getThumbnail())
                .placeholder(new ColorDrawable(Color.GRAY))
                .transition(withCrossFade())
                .into(viewHolder.imageView);

        viewHolder.textTitle.setText(historyArrayList.get(i).getTitle());
        if (historyArrayList.get(i).getIsLive()) {
            viewHolder.textLive.setVisibility(View.VISIBLE);
        }else{
            viewHolder.textDuration.setText(historyArrayList.get(i).getDuration());
            viewHolder.textDuration.setVisibility(View.VISIBLE);
            viewHolder.textLive.setVisibility(View.GONE);
        }
        viewHolder.textChannel.setText(historyArrayList.get(i).getChannelName());
        viewHolder.textViews.setText(historyArrayList.get(i).getViewCounts());
    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView cardView;
        ImageView imageView;
        TextView textTitle, textChannel, textViews, textLive, textDuration;
        MaterialCheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_search);
            imageView = itemView.findViewById(R.id.imageView);
            textTitle = itemView.findViewById(R.id.title);
            textChannel = itemView.findViewById(R.id.text_channel);
            textViews = itemView.findViewById(R.id.text_views);
            textLive = itemView.findViewById(R.id.text_live);
            textDuration = itemView.findViewById(R.id.text_duration);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void clearSelections() {
        showCheckBoxes = false;
        liveDataIsSelectedAll.setValue(false);
        notifyDataSetChanged();
    }

    public Boolean getShowCheckBoxes() {
        return showCheckBoxes;
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(){
        showCheckBoxes = true;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void check(Boolean isChecked) {
        this.isChecked = isChecked;
        notifyDataSetChanged();
    }
    public interface onLongClickListener{
        void onLongClick();
    }

    public MutableLiveData<Boolean> getLiveDataIsSelectedAll() {
        return liveDataIsSelectedAll;
    }

    public MutableLiveData<Pair<Integer, Boolean>> getSelectedItems() {
        return selectedItems;
    }
}
