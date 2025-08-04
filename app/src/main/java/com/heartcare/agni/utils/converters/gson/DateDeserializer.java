package com.heartcare.agni.utils.converters.gson;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {

    private static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd HH:mm:ssZ",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    };

    @NonNull
    @Override
    public Date deserialize(@NonNull JsonElement jsonElement, @NonNull Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        String dateStr = jsonElement.getAsString();

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                // If format has explicit timezone info, parse using that
                if (format.contains("Z") || format.contains("X") || format.contains("z")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // parses to UTC
                } else {
                    // If no timezone info in format or string, treat it as IST (or system default)
                    sdf.setTimeZone(TimeZone.getDefault());
                }

                Date date = sdf.parse(dateStr);
                if (date != null) return date;

            } catch (ParseException ignored) {
                // Try next format
            }
        }

        throw new JsonParseException("Unparseable date: \"" + dateStr
                + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
    }
}