package com.joek.nuxtube.listeners;

import com.joek.nuxtube.data.model.search.SearchItem;

public class SearchClickListener {
    public interface OnItemClickListener {
        void onItemClick(SearchItem searchItem);
    }
}
