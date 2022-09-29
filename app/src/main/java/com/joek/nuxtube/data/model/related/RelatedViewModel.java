package com.joek.nuxtube.data.model.related;

import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joek.nuxtube.data.model.search.SearchItem;
import com.joek.nuxtube.network.TaskRunner;
import com.joek.nuxtube.parsers.RelatedParser;

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

public class RelatedViewModel extends ViewModel {

    private String RELATED_API = "https://www.youtube.com/youtubei/v1/next?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";

    private final MutableLiveData<ArrayList<RelatedItem>> relatedResults = new MutableLiveData<>();
    private final MutableLiveData<String> relatedError = new MutableLiveData<>();

    public void makeRequest(String videoId) throws JSONException {
        TaskRunner.getInstance().executeCallable(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return fetchRelatedResults(videoId);
            }
        }, new TaskRunner.OnCompletedCallback<Response>() {
            @Override
            public void onComplete(@Nullable Response result) {
                try {
                    parseJsonBody(result.body().string());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    relatedError.setValue(e.getMessage());
                }
            }
        });

    }

    private void parseJsonBody(String body) throws JSONException {
        RelatedParser relatedParser = new RelatedParser();
        relatedParser.parseRelatedResults(body);
        relatedResults.setValue(relatedParser.getRelatedResults());
    }

    private Response fetchRelatedResults(String videoId) throws JSONException, IOException {
        final OkHttpClient okClient = new OkHttpClient();
        JSONObject jsonBody = new JSONObject(), context = new JSONObject(), client = new JSONObject();
        client.put("clientName", "WEB");
        client.put("clientVersion", "2.20201021.03.00");

        context.put("client", client);
        jsonBody.put("context", context);
        jsonBody.put("videoId", videoId);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(RELATED_API)
                .post(body)
                .build();

        return okClient.newCall(request).execute();
    }

    public MutableLiveData<ArrayList<RelatedItem>> getRelatedResults() {
        return relatedResults;
    }
    public void clearRelatedResults() {
        if (relatedResults.getValue() != null){
            relatedResults.getValue().clear();
        }

    }
}
