package com.joek.nuxtube.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.joek.nuxtube.R;

public class CustomStyledPlayerView extends StyledPlayerView {
    private ImageView artworkView;

    public CustomStyledPlayerView(Context context) {
        super(context);
        Init();
    }

    public CustomStyledPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public CustomStyledPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void Init(){
        artworkView = findViewById(com.google.android.exoplayer2.ui.R.id.exo_artwork);
        artworkView.setVisibility(VISIBLE);
        artworkView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
    }

    @Override
    public boolean getUseArtwork() {
        return true;
    }
}
