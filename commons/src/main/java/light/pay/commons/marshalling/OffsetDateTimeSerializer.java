package light.pay.commons.marshalling;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return OffsetDateTime.parse(json.getAsString());
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
