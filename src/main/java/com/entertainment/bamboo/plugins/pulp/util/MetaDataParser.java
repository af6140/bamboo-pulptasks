package com.entertainment.bamboo.plugins.pulp.util;

import com.entertainment.bamboo.plugins.pulp.model.puppet.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by dwang on 9/1/16.
 */
public class MetaDataParser {
    public static Metadata parseJsonString(String json) {
        Metadata metaData =null;
        GsonBuilder builder =new GsonBuilder();
        //builder.excludeFieldsWithoutExposeAnnotation();
        builder.registerTypeAdapter(ModuleName.class, new ModuleName.JsonAdapter());
        Gson gson = builder.create();
        //JsonParser parser= new JsonParser();
        //JsonObject o=  parser.parse(json).getAsJsonObject();
        metaData = gson.fromJson(json, Metadata.class);
        return  metaData;
    }
}
