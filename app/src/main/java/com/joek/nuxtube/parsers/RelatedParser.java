package com.joek.nuxtube.parsers;

import com.joek.nuxtube.data.model.related.RelatedItem;
import com.joek.nuxtube.data.model.search.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RelatedParser {

    ArrayList<RelatedItem> relatedResults = new ArrayList<>();

    public void parseRelatedResults(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        parseSecondaryResults(jsonObject);
    }

    private void parseSecondaryResults(JSONObject jsonObject) throws JSONException {
        JSONArray secondaryContents = jsonObject.getJSONObject("contents")
                .getJSONObject("twoColumnWatchNextResults")
                .getJSONObject("secondaryResults")
                .getJSONObject("secondaryResults")
                .getJSONArray("results");

        for (int i = 0; i < secondaryContents.length(); i++) {
            if (!secondaryContents.getJSONObject(i).isNull("compactVideoRenderer")){
                parseCompactVideoRenderer(secondaryContents.getJSONObject(i).getJSONObject("compactVideoRenderer"));
            }
        }
    }

    private void parseCompactVideoRenderer(JSONObject videoRenderer) throws JSONException {
        String videoId = videoRenderer.getString("videoId");
        String title = videoRenderer.getJSONObject("title").get("simpleText").toString();
        JSONArray thumbnails = videoRenderer.getJSONObject("thumbnail").getJSONArray("thumbnails");
        String thumbnail = thumbnails.getJSONObject(thumbnails.length()-1).get("url").toString();
        String channelName = videoRenderer.getJSONObject("shortBylineText").getJSONArray("runs").getJSONObject(0).get("text").toString();

        String viewCounts = "";
        if (!videoRenderer.isNull("shortViewCountText")){
            if (!videoRenderer.getJSONObject("shortViewCountText").isNull("simpleText")){
                viewCounts = videoRenderer.getJSONObject("shortViewCountText").get("simpleText").toString();
            }else {
                viewCounts = jsonArrayToString(videoRenderer.getJSONObject("shortViewCountText").getJSONArray("runs"));
            }
        }
        String publishedAt = (!videoRenderer.isNull("publishedTimeText") ? videoRenderer.getJSONObject("publishedTimeText").get("simpleText") : "0").toString();
        String lengthText = (!videoRenderer.isNull("lengthText") ? videoRenderer.getJSONObject("lengthText").get("simpleText") : "").toString();
        String avatarUrl = videoRenderer.getJSONObject("channelThumbnail").getJSONArray("thumbnails").getJSONObject(0).get("url").toString();
        String channelId = videoRenderer.getJSONObject("shortBylineText").getJSONArray("runs").getJSONObject(0).getJSONObject("navigationEndpoint").getJSONObject("browseEndpoint").get("browseId").toString();
        Boolean isLive = !videoRenderer.isNull("badges") && videoRenderer.getJSONArray("badges").getJSONObject(0).getJSONObject("metadataBadgeRenderer").getString("label").equals("LIVE");

        relatedResults.add(new RelatedItem(videoId, thumbnail, title, viewCounts, publishedAt, lengthText, channelName, channelId, avatarUrl, isLive));
    }

    private String jsonArrayToString(JSONArray jsonArray) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringBuilder.append(jsonArray.getJSONObject(i).getString("text"));
        }
        return stringBuilder.toString();
    }
    public ArrayList<RelatedItem> getRelatedResults() {
        return relatedResults;
    }
}
