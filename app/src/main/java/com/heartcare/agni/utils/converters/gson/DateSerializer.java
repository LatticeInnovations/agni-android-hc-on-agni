package com.heartcare.agni.utils.converters.gson;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateSerializer implements JsonSerializer<Date> {

    @NonNull
    @Override
    public JsonElement serialize(@NonNull Date src, @NonNull Type typeOfSrc, @NonNull JsonSerializationContext context) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new JsonPrimitive(formatter.format(src));
    }
}
