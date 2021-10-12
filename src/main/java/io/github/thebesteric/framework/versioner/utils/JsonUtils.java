package io.github.thebesteric.framework.versioner.utils;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

    static JsonParser jsonParser = new JsonParser();

    public static JsonObject toJsonNode(String content) {
        return jsonParser.parse(content).getAsJsonObject();
    }
}
