package com.joek.nuxtube.parsers;

import com.joek.nuxtube.data.model.search.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchParser {
    ArrayList<String> refinments = new ArrayList<>();
    ArrayList<SearchItem> searchResults = new ArrayList<>();

    public void parseSearchResults(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        parsePrimary(jsonObject);
    }
    private void parsePrimary(JSONObject jsonObject) throws JSONException {
        JSONArray primaryContents = jsonObject.getJSONObject("contents")
                .getJSONObject("twoColumnSearchResultsRenderer")
                .getJSONObject("primaryContents")
                .getJSONObject("sectionListRenderer")
                .getJSONArray("contents");


        JSONArray itemSectionRenderers = primaryContents.getJSONObject(0)
                .getJSONObject("itemSectionRenderer")
                .getJSONArray("contents");

        for (int i = 0; i < itemSectionRenderers.length(); i++) {
            if (!itemSectionRenderers.getJSONObject(i).isNull("videoRenderer")){
                parseVideoRenderer(itemSectionRenderers.getJSONObject(i).getJSONObject("videoRenderer"));
            }
        }

    }

    private void parseVideoRenderer(JSONObject videoRenderer) throws JSONException {
        String videoId = videoRenderer.getString("videoId");
        String title = videoRenderer.getJSONObject("title").getJSONArray("runs").getJSONObject(0).get("text").toString();
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
        String avatarUrl = videoRenderer.getJSONObject("channelThumbnailSupportedRenderers").getJSONObject("channelThumbnailWithLinkRenderer").getJSONObject("thumbnail").getJSONArray("thumbnails").getJSONObject(0).get("url").toString();
        String supportingText = (!videoRenderer.isNull("detailedMetadataSnippets") ? String.valueOf(videoRenderer.getJSONArray("detailedMetadataSnippets").getJSONObject(0).getJSONObject("snippetText").getJSONArray("runs").getJSONObject(0).get("text")) : "");
        Boolean isLive = !videoRenderer.isNull("badges") && videoRenderer.getJSONArray("badges").getJSONObject(0).getJSONObject("metadataBadgeRenderer").getString("label").equals("LIVE");

        searchResults.add(new SearchItem(videoId, thumbnail, title, viewCounts, publishedAt, lengthText, channelName, supportingText, avatarUrl, isLive));
    }

    private String jsonArrayToString(JSONArray jsonArray) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            stringBuilder.append(jsonArray.getJSONObject(i).getString("text"));
        }
        return stringBuilder.toString();
    }
    public ArrayList<SearchItem> getSearchResults() {
        return searchResults;
    }
}
