package com.joek.nuxtube.providers;

import android.content.SearchRecentSuggestionsProvider;
import android.util.Log;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = MySuggestionProvider.class.getName();
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        Log.e("hi", AUTHORITY);
        setupSuggestions(AUTHORITY, MODE);
    }
}
