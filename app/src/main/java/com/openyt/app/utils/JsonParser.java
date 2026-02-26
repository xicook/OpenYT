package com.openyt.app.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.openyt.app.models.UpdateMessage;
import com.openyt.app.models.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitário para parsear as respostas JSON da API OpenYT.
 */
public class JsonParser {

    /** Converte um JsonObject de vídeo em objeto Video */
    public static Video parseVideo(JsonObject obj) {
        Video video = new Video();
        video.setId(getString(obj, "id"));
        video.setTitle(getString(obj, "title"));
        video.setChannel(getString(obj, "channel"));
        video.setChannelId(getString(obj, "channelId"));
        video.setThumbnail(getString(obj, "thumbnail"));
        video.setDuration(getString(obj, "duration"));
        video.setDescription(getString(obj, "description"));
        video.setUploadDate(getString(obj, "uploadDate"));

        if (obj.has("views") && !obj.get("views").isJsonNull()) {
            video.setViews(obj.get("views").getAsLong());
        }
        if (obj.has("likes") && !obj.get("likes").isJsonNull()) {
            video.setLikes(obj.get("likes").getAsLong());
        }

        return video;
    }

    /** Converte um JsonArray de vídeos em List<Video> */
    public static List<Video> parseVideoList(JsonArray array) {
        List<Video> list = new ArrayList<>();
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                list.add(parseVideo(element.getAsJsonObject()));
            }
        }
        return list;
    }

    /** Converte um JsonObject de mensagem em UpdateMessage */
    public static UpdateMessage parseUpdateMessage(JsonObject obj) {
        UpdateMessage msg = new UpdateMessage();
        if (obj.has("id")) msg.setId(obj.get("id").getAsInt());
        msg.setText(getString(obj, "text"));
        msg.setTimestamp(getString(obj, "timestamp"));
        return msg;
    }

    /** Converte um JsonArray de mensagens em List<UpdateMessage> */
    public static List<UpdateMessage> parseUpdateList(JsonArray array) {
        List<UpdateMessage> list = new ArrayList<>();
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                list.add(parseUpdateMessage(element.getAsJsonObject()));
            }
        }
        return list;
    }

    /** Helper: retorna string ou "" se não existir */
    private static String getString(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return "";
    }
}
