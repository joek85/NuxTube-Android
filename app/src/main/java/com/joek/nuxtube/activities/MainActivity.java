package com.joek.nuxtube.activities;

import static android.app.SearchManager.SUGGEST_COLUMN_TEXT_1;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static com.joek.nuxtube.R.drawable.search_suggestions_dividers;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.transition.AutoTransition;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.joek.nuxtube.R;
import com.joek.nuxtube.adapters.PlayerAdapter;
import com.joek.nuxtube.animations.SizeChangeAnimation;
import com.joek.nuxtube.data.model.history.History;
import com.joek.nuxtube.data.model.history.HistoryViewModel;
import com.joek.nuxtube.data.model.related.RelatedItem;
import com.joek.nuxtube.data.model.related.RelatedViewModel;
import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.databinding.ActivityMainBinding;
import com.joek.nuxtube.extracters.youtubeExtractor.VideoMeta;
import com.joek.nuxtube.extracters.youtubeExtractor.YouTubeExtractor;
import com.joek.nuxtube.extracters.youtubeExtractor.YtFile;
import com.joek.nuxtube.listeners.RelatedClickListener;
import com.joek.nuxtube.listeners.SearchClickListener;
import com.joek.nuxtube.network.TaskRunner;
import com.joek.nuxtube.providers.MySuggestionProvider;
import com.joek.nuxtube.ui.CustomStyledPlayerView;
import com.joek.nuxtube.ui.bottomsheets.VideoDetailsBottomSheet;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements SearchClickListener.OnItemClickListener, RelatedClickListener.OnItemClickListener, Player.Listener {
    String SEARCH_SUGGESTION_API = "https://suggestqueries.google.com/complete/search";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    BottomSheetBehavior<View> bottomSheetBehavior;
    NavController navController;
    private View bottomSheet;
    ImageView top_image, avatar_image;
    TextView textTitle, textSecondary, text_channel, text_mini_title, text_mini_secondary;
    RecyclerView recyclerView_Player;
    ProgressBar progress_related;
    RelatedViewModel relatedViewModel;
    String videoId = "";
    ExoPlayer player;
    CustomStyledPlayerView playerView;
    private final ArrayList<RelatedItem> relatedItems = new ArrayList<>();
    PlayerAdapter playerAdapter;
    ImageButton btn_play, btn_close;
    LinearLayout layout_panel;
    HistoryViewModel historyViewModel;
    AppBarLayout appBarLayout;
    VideoDetailsBottomSheet videoDetailsBottomSheet;
    SearchRecentSuggestions suggestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //startService(new Intent(this, NetworkService.class));

        setSupportActionBar(binding.appBarMain.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        bottomSheet = findViewById(R.id.bottomSheet);
        //top_image = findViewById(R.id.top_image);
        avatar_image = findViewById(R.id.avatar_image);
        textTitle = findViewById(R.id.title);
        textSecondary = findViewById(R.id.secondary);
        text_channel = findViewById(R.id.text_channel);
        text_mini_title = findViewById(R.id.text_mini_title);
        text_mini_secondary = findViewById(R.id.text_mini_secondary);
        progress_related = findViewById(R.id.progress_related);
        btn_play = findViewById(R.id.image_play);
        btn_close = findViewById(R.id.image_clear);
        layout_panel = findViewById(R.id.linear_layout);

        recyclerView_Player = findViewById(R.id.recyclerview);
        recyclerView_Player.setLayoutManager(new LinearLayoutManager(this));
        playerAdapter = new PlayerAdapter(relatedItems, this, this);

        recyclerView_Player.setAdapter(playerAdapter);


        playerView = findViewById(R.id.player_view);
        setupPlayerView();

        setupBottomSheet();
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_history,
                R.id.nav_search)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //navController.setGraph(R.navigation.mobile_navigation, null);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        relatedViewModel = new ViewModelProvider(this).get(RelatedViewModel.class);

        relatedViewModel.getRelatedResults().observe(this, searchItems -> {
            relatedItems.clear();
            relatedItems.addAll(searchItems);
            playerAdapter.notifyItemInserted(relatedItems.size() - 1);
            progress_related.setVisibility(View.GONE);
        });

        btn_close.setOnClickListener(view -> bottomSheetBehavior.setState(STATE_HIDDEN));
        btn_play.setOnClickListener(view -> {
            if (player.isPlaying()){
                player.pause();
            }else{
                player.play();
            }
        });
        videoDetailsBottomSheet = new VideoDetailsBottomSheet();

        layout_panel.setOnClickListener(view -> videoDetailsBottomSheet.show(getSupportFragmentManager(), ""));
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("hi", "Activity resume");
        //bottomSheetBehavior.setState(bottomSheetBehavior.getLastStableState());

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("hi", "Activity pause");

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottomSheetState", bottomSheetBehavior.getState());
        Log.e("hi", "Activity save state");
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("hi", "Activity restore state");
        if (savedInstanceState != null) {
            Log.e("hi", String.valueOf(savedInstanceState.getInt("bottomSheetState")));
            bottomSheetBehavior.setState(savedInstanceState.getInt("bottomSheetState", STATE_HIDDEN));
        }
    }

    private void setupPlayerView(){
        playerView.setControllerShowTimeoutMs(2000);
        playerView.setUseArtwork(true);
        playerView.setKeepContentOnPlayerReset(true);
        playerView.setShowNextButton(false);
        playerView.setShowPreviousButton(false);

        DefaultRenderersFactory renderersFactory =
                new DefaultRenderersFactory(getApplicationContext())
                        .forceEnableMediaCodecAsynchronousQueueing();
        //top_image = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_artwork);
        player = new ExoPlayer.Builder(this, renderersFactory)
                .build();

        player.addListener(this);
        playerView.setPlayer(player);
    }
    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //bottomSheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);
        //MotionLayout motionLayout = findViewById(R.id.motionLayout);

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_mini);
        SizeChangeAnimation sizeChangeAnimationExo = new SizeChangeAnimation(playerView);
        SizeChangeAnimation sizeChangeAnimationMini = new SizeChangeAnimation(constraintLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == STATE_HIDDEN){
                    player.stop();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //motionLayout.setProgress(1-slideOffset);

                if (slideOffset >= 0) {
                    int width = appBarLayout.getWidth();
                    int height = appBarLayout.getHeight();

                    sizeChangeAnimationExo.withWidthScale(true);
                    sizeChangeAnimationExo.setWidths((int) (width * ((-slideOffset+1) * 0.65f)), width, slideOffset);
                    sizeChangeAnimationExo.setHeights((int) (height * ((-slideOffset+1)* 0.70f)), height);

                    playerView.startAnimation(sizeChangeAnimationExo);

                    sizeChangeAnimationMini.setX(width * ((-slideOffset+1))*0.355f);
                    sizeChangeAnimationMini.setWidths((int) (width * ((-slideOffset+1) * 0.35f)), (int) (width));
                    sizeChangeAnimationMini.setHeights((int) (height * ((-slideOffset+1) * 0.7f)), (int) (height));

                    constraintLayout.setAlpha(-slideOffset + 1);
                    constraintLayout.startAnimation(sizeChangeAnimationMini);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//
////            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
////                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
////            suggestions.saveRecentQuery(query, null);
//
//            Log.e("hi", query);
//            //doMySearch(query);
//        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchMenu = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
//        searchView.setShowDividers(LinearLayoutCompat.SHOW_DIVIDER_MIDDLE | LinearLayoutCompat.SHOW_DIVIDER_BEGINNING | LinearLayoutCompat.SHOW_DIVIDER_END);
//        searchView.setDividerDrawable(getDrawable(search_suggestions_dividers));

        //searchView.setQueryRefinementEnabled(true);
        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor query = searchView.getSuggestionsAdapter().getCursor();
                if (query.moveToPosition(position)) {
                    @SuppressLint("Range") String name = query.getString(query.getColumnIndex(SUGGEST_COLUMN_TEXT_1));
                    searchView.setQuery(name, true);
                    suggestions.saveRecentQuery(name, null);
                }
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                suggestions.saveRecentQuery(query, null);
                Bundle bundle = new Bundle();
                bundle.putString("query", query);
                navController.navigateUp();
                navController.navigate(R.id.nav_search, bundle);
                searchView.clearFocus();
                suggestions.saveRecentQuery(query, null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                TaskRunner.getInstance().executeCallable(() -> fetchSearchSuggestions(newText), result -> {
                    try {
                        String[] columns = {
                                BaseColumns._ID,
                                SUGGEST_COLUMN_TEXT_1,
                                SearchManager.SUGGEST_COLUMN_INTENT_DATA};

                        MatrixCursor cursor = new MatrixCursor(columns);
                        String response = result.body().string();
                        String suggestions = response.substring(3, response.length()-1);

                        try {
                            final JsonArray collection = JsonParser.array().from(suggestions).getArray(1);
                            for (int i = 0; i < collection.size(); i++) {
                                Object suggestion = collection.get(i);
                                if (!(suggestion instanceof JsonArray)) {
                                    continue;
                                }
                                final String suggestionStr = ((JsonArray) suggestion).getString(0);
                                if (suggestionStr == null) {
                                    continue;
                                }
                                String[] tmp = {Integer.toString(i), suggestionStr, "COLUMNT_INTENT_TEXT_1"};
                                cursor.addRow(tmp);
                            }
                        } catch (final JsonParserException e) {
                            Log.e("hi", e.getMessage());
                        }

                        suggestionAdapter.changeCursor(cursor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
//                Cursor cursor = getRecents(newText);
//                if (cursor != null) {
//                    suggestionAdapter.swapCursor(cursor);
//                }

                return false;
            }
        });

        return true;
    }
    private Cursor getRecents(String query){

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(MySuggestionProvider.AUTHORITY);
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        String selection = " ?";
        String[] selArgs = new String[1];
        selArgs[0] = query;
        Uri uri = uriBuilder.build();
        return this.getContentResolver().query(uri, null, selection, selArgs, null);
    }
    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showBottomSheet(){
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }
    @Override
    public void onItemClick(SearchItem searchItem) {
        textTitle.setText(searchItem.getTitle());
        text_mini_title.setText(searchItem.getTitle());
        text_mini_secondary.setText(searchItem.getChannelText());
        if (searchItem.getLive()) {
            textSecondary.setText(searchItem.getViewCounts());
        }else{
            String secondary = searchItem.getViewCounts() + " . " + searchItem.getPublishedAt() + " . " + searchItem.getLengthText();
            textSecondary.setText(secondary);
        }

        text_channel.setText(searchItem.getChannelText());

//        Glide.with(getApplicationContext()).load(searchItem.getImageUrl())
//                .transition(withCrossFade())
//                .into(top_image);
        Glide.with(getApplicationContext()).load(searchItem.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(avatar_image);

        historyViewModel.insert(new History(searchItem.getVideoId(), searchItem.getTitle(), searchItem.getChannelText(), "",
                searchItem.getLengthText(), searchItem.getViewCounts(), searchItem.getPublishedAt(),
                searchItem.getImageUrl(), searchItem.getAvatarUrl(), searchItem.getLive()));
        showBottomSheet();
        getRelatedVideos(searchItem.getVideoId());
        initPlayback(searchItem.getVideoId(), searchItem.getImageUrl(), searchItem.getLive());
    }

    private void getRelatedVideos(String videoId) {

        if (!this.videoId.equals(videoId)) {
            progress_related.setVisibility(View.VISIBLE);
            relatedItems.clear();
            playerAdapter.notifyDataSetChanged();
            this.videoId = videoId;
            try {
                relatedViewModel.makeRequest(videoId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(RelatedItem relatedItem) {
        textTitle.setText(relatedItem.getTitle());
        text_mini_title.setText(relatedItem.getTitle());
        text_mini_secondary.setText(relatedItem.getChannelName());
        String secondary = relatedItem.getViewCounts() + " . " + relatedItem.getPublishedAt() + " . " + relatedItem.getLengthText();
        textSecondary.setText(secondary);
        text_channel.setText(relatedItem.getChannelName());

//        Glide.with(getApplicationContext()).load(relatedItem.getThumbnail())
//                .transition(withCrossFade())
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(top_image);
        Glide.with(getApplicationContext()).load(relatedItem.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(avatar_image);

        historyViewModel.insert(new History(relatedItem.getVideoId(), relatedItem.getTitle(), relatedItem.getChannelName(), "",
                relatedItem.getLengthText(), relatedItem.getViewCounts(), relatedItem.getPublishedAt(),
                relatedItem.getThumbnail(), relatedItem.getAvatarUrl(), relatedItem.getLive()));
        showBottomSheet();
        initPlayback(relatedItem.getVideoId(), relatedItem.getThumbnail(), false);
        getRelatedVideos(relatedItem.getVideoId());
    }

    private void initPlayback(String videoId, String imageUrl, Boolean isLive) {
        YouTubeExtractor youTubeExtractor = new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete( SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
//                ImageView imageView = playerView.getRootView().findViewById(com.google.android.exoplayer2.ui.R.id.exo_artwork);
                Log.e("hi", String.valueOf(videoMeta));
                videoDetailsBottomSheet.setDescription(videoMeta);
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                runOnUiThread(() -> {
                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, appBarLayout.getWidth(), appBarLayout.getHeight(), true));
                                    playerView.setDefaultArtwork(d);
                                    bitmap.recycle();
                                });
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

                if (isLive) {
//                    MediaItem mediaItem =
//                            new MediaItem.Builder()
//                                    .setUri(ytFiles.get(140).getUrl())
//                                    .setMimeType(MimeTypes.AUDIO_WEBM)
//                                    .setLiveConfiguration(
//                                            new MediaItem.LiveConfiguration.Builder()
//                                                    .setMaxPlaybackSpeed(1.02f)
//                                                    .build())
//                                    .build();
                    //player.setMediaItem(MediaItem.fromUri(ytFiles.get(140).getUrl()));
                    com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();

//                    MediaSource mediaSource =
//                            new DashMediaSource.Factory(dataSourceFactory)
//                                    .createMediaSource(MediaItem.fromUri(ytFiles.get(140).getUrl()));
//                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                            .createMediaSource(MediaItem.fromUri(ytFiles.get(140).getUrl()));
//                    player.setMediaSource(mediaSource);
                }else{
//                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
//                            .createMediaSource(MediaItem.fromUri(ytFiles.get(140).getUrl()));
//                    player.setMediaSource(mediaSource);
                }

//                player.prepare();
//                player.play();
            }
        };
        youTubeExtractor.extract(videoId);
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        playerView.setErrorMessageProvider(throwable -> new Pair<>(error.errorCode, error.getMessage()));
    }
    private Response fetchSearchSuggestions(String searchString) throws IOException {
        final OkHttpClient okClient = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(SEARCH_SUGGESTION_API).newBuilder();
        httpBuilder.addQueryParameter("client", "youtube");
        httpBuilder.addQueryParameter("jsonp", "JP");
        httpBuilder.addQueryParameter("ds", "yt");
        httpBuilder.addQueryParameter("gl", "US");
        httpBuilder.addQueryParameter("q", searchString);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        return okClient.newCall(request).execute();
    }
    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        Player.Listener.super.onIsPlayingChanged(isPlaying);
        if (isPlaying) {
            btn_play.setBackground(AppCompatResources.getDrawable(this,R.drawable.ic_baseline_pause_24));
        }else{
            btn_play.setBackground(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_play_arrow_24));

        }
    }
}