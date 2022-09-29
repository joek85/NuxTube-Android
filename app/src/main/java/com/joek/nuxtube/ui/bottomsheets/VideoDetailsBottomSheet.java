package com.joek.nuxtube.ui.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;
import com.joek.nuxtube.R;
import com.joek.nuxtube.extracters.youtubeExtractor.VideoMeta;

import java.util.Objects;

public class VideoDetailsBottomSheet extends BottomSheetDialogFragment {
    private MutableLiveData<VideoMeta> videoMeta = new MutableLiveData<>();
    public VideoDetailsBottomSheet() {
        super();
    }

    public void setDescription(VideoMeta videoMeta) {
        this.videoMeta.setValue(videoMeta);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            MaterialTextView textDescription = d.findViewById(R.id.description);
            MaterialTextView textTitle = d.findViewById(R.id.title);
            MaterialTextView textSecondary = d.findViewById(R.id.secondary);
            ImageView imageAvatar = d.findViewById(R.id.avatar_image);
            ProgressBar progressBar = d.findViewById(R.id.progress_bar);
            TextView textViews = d.findViewById(R.id.txtViews);
            TextView textLength = d.findViewById(R.id.txtLength);
            TextView textPublished = d.findViewById(R.id.txtDate);

            assert progressBar != null;
            progressBar.setVisibility(View.VISIBLE);

            videoMeta.observe(getViewLifecycleOwner(), videoMeta -> {
                assert textTitle != null;
                textTitle.setText(videoMeta.getTitle());
                assert textSecondary != null;
                textSecondary.setText(videoMeta.getAuthor());
                assert textDescription != null;
                textDescription.setText(videoMeta.getShortDescription());

                assert imageAvatar != null;
                Glide.with(requireContext())
                        .load(videoMeta.getThumbUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageAvatar);

                assert textLength != null;
                textLength.setText(String.valueOf(videoMeta.getVideoLength()));
                assert textViews != null;
                textViews.setText(String.valueOf(videoMeta.getViewCount()));
                assert textPublished != null;
                textPublished.setText(videoMeta.getPublished());
                progressBar.setVisibility(View.GONE);
            });

            assert bottomSheetInternal != null;
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
            bottomSheetBehavior.setFitToContents(true);
            bottomSheetBehavior.setDraggable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });

        return inflater.inflate(R.layout.bottom_sheet_video_details,container);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialog;
    }
}
