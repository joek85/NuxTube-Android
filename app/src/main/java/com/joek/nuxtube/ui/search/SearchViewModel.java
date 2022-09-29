package com.joek.nuxtube.ui.search;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.network.TaskRunner;
import com.joek.nuxtube.parsers.SearchParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchViewModel extends ViewModel {
    String SEARCH_API = "https://www.youtube.com/youtubei/v1/search?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";

    private final MutableLiveData<ArrayList<SearchItem>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> searchError = new MutableLiveData<>();

    public SearchViewModel() {
    }
    public void makeRequest(String searchString) throws JSONException {

        TaskRunner.getInstance().executeCallable(() -> fetchSearchResults(searchString), result -> {
            try {
                parseJsonBody(result.body().string());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                searchError.setValue(e.getMessage());
            }
        });

    }
    private void parseJsonBody(String body) throws JSONException {
        SearchParser searchParser = new SearchParser();
        searchParser.parseSearchResults(body);
        searchResults.setValue(searchParser.getSearchResults());
    }

    public MutableLiveData<ArrayList<SearchItem>> getSearchResults() {
        return searchResults;
    }

    public MutableLiveData<String> getSearchError() {
        return searchError;
    }

    private Response fetchSearchResults(String searchString) throws JSONException, IOException {
        final OkHttpClient okClient = new OkHttpClient();
        JSONObject jsonBody = new JSONObject(), context = new JSONObject(), client = new JSONObject();
        client.put("clientName", "WEB");
        client.put("clientVersion", "2.20201021.03.00");

        context.put("client", client);
        jsonBody.put("context", context);
        jsonBody.put("query", searchString);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(SEARCH_API)
                .post(body)
                .build();

        return okClient.newCall(request).execute();
    }
}