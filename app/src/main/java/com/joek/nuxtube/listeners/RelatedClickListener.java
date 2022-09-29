package com.joek.nuxtube.listeners;

import com.joek.nuxtube.data.model.related.RelatedItem;
public class RelatedClickListener {
    public interface OnItemClickListener {
        void onItemClick(RelatedItem relatedItem);
    }
}
