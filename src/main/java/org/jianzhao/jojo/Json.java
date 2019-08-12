package org.jianzhao.jojo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public class Json {

    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        SimpleModule module = new SimpleModule();
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        module.addSerializer(Instant.class, new InstantSerializer());
        module.addSerializer(Date.class, new DateSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());
        mapper.registerModule(module);
    }

    public static String encode(Object obj) throws JojoException {
        return apply(() -> mapper.writeValueAsString(obj));
    }

    public static ByteBuffer encodeToBuffer(Object obj) throws JojoException {
        return apply(() -> ByteBuffer.wrap((mapper.writeValueAsBytes(obj))));
    }

    public static <T> T decodeValue(String str, Class<T> clazz) throws JojoException {
        return apply(() -> mapper.readValue(str, clazz));
    }

    public static <T> T decodeValue(String str, TypeReference<T> type) throws JojoException {
        return apply(() -> mapper.readValue(str, type));

    }

    public static <T> T decodeValue(ByteBuffer buf, TypeReference<T> type) throws JojoException {
        return apply(() -> mapper.readValue(buf.array(), type));
    }

    public static <T> T decodeValue(ByteBuffer buf, Class<T> clazz) throws JojoException {
        return apply(() -> mapper.readValue(buf.array(), clazz));
    }

    public static JsonObject flatten(JsonObjectOrJsonArray<?> jsonObjectOrJsonArray) {
        JsonObject result = new JsonObject();
        recurseFlatten(jsonObjectOrJsonArray.get(), "", result);
        return result;
    }

    private static void recurseFlatten(Object current, String key, JsonObject result) {
        if (current instanceof JsonObject) {
            if (((JsonObject) current).isEmpty()) {
                result.put(key, new JsonObject());
            } else {
                for (Map.Entry<String, Object> entry : ((JsonObject) current)) {
                    recurseFlatten(entry.getValue(), key.length() > 0 ? key + "." + entry.getKey() : entry.getKey(), result);
                }
            }
        } else if (current instanceof JsonArray) {
            if (((JsonArray) current).isEmpty()) {
                result.put(key, new JsonArray());
            } else {
                for (int i = 0; i < ((JsonArray) current).size(); i++) {
                    recurseFlatten(((JsonArray) current).getValue(i), key.length() > 0 ? key + "[" + i + "]" : "[" + i + "]", result);
                }
            }
        } else {
            result.put(key, current);
        }
    }

    public static JsonObjectOrJsonArray<?> unFlatten(JsonObject jsonObject) {
        JsonObjectOrJsonArray result = null;
        JsonObjectOrJsonArray current = null;
        for (Map.Entry<String, Object> entry : jsonObject) {
            KeyVernier vernier = new KeyVernier(entry.getKey());
            while (vernier.hasNext()) {
                vernier.next();
                if (vernier.isCurrentTypeObject()) {
                    if (result == null) {
                        result = JsonObjectOrJsonArray.of(new JsonObject());
                        current = result;
                    }
                    JsonObject o = (JsonObject) current.get();
                    if (!vernier.hasNext()) {
                        o.put(vernier.currentKey(), entry.getValue());
                    } else if (vernier.isNextTypeArray()) {
                        JsonArray nextJsonArray = o.getJsonArray(vernier.currentKey());
                        if (nextJsonArray == null) {
                            nextJsonArray = new JsonArray();
                            o.put(vernier.currentKey(), nextJsonArray);
                        }
                        current = JsonObjectOrJsonArray.of(nextJsonArray);
                    } else if (vernier.isNextTypeObject()) {
                        JsonObject nextJsonObject = o.getJsonObject(vernier.currentKey());
                        if (nextJsonObject == null) {
                            nextJsonObject = new JsonObject();
                            o.put(vernier.currentKey(), nextJsonObject);
                        }
                        current = JsonObjectOrJsonArray.of(nextJsonObject);
                    }
                } else if (vernier.isCurrentTypeArray()) {
                    if (result == null) {
                        result = JsonObjectOrJsonArray.of(new JsonArray());
                        current = result;
                    }
                    JsonArray a = (JsonArray) current.get();
                    if (!vernier.hasNext()) {
                        checkAndSetJsonArrayItem(a, vernier.currentIndex(), entry.getValue());
                    } else if (vernier.isNextTypeArray()) {
                        ensureJsonArrayCapacity(a, vernier.currentIndex() + 1);
                        JsonArray nextJsonArray = a.getJsonArray(vernier.currentIndex());
                        if (nextJsonArray == null) {
                            nextJsonArray = new JsonArray();
                            checkAndSetJsonArrayItem(a, vernier.currentIndex(), nextJsonArray);
                        }
                        current = JsonObjectOrJsonArray.of(nextJsonArray);
                    } else if (vernier.isNextTypeObject()) {
                        ensureJsonArrayCapacity(a, vernier.currentIndex() + 1);
                        JsonObject nextJsonObject = a.getJsonObject(vernier.currentIndex());
                        if (nextJsonObject == null) {
                            nextJsonObject = new JsonObject();
                            checkAndSetJsonArrayItem(a, vernier.currentIndex(), nextJsonObject);
                        }
                        current = JsonObjectOrJsonArray.of(nextJsonObject);
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    static void ensureJsonArrayCapacity(JsonArray jsonArray, int capacity) {
        List<Object> list = jsonArray.getList();
        while (list.size() < capacity) {
            list.add(null);
        }
    }

    @SuppressWarnings("unchecked")
    static void checkAndSetJsonArrayItem(JsonArray jsonArray, int index, Object value) {
        ensureJsonArrayCapacity(jsonArray, index + 1);
        Object val = checkAndCopy(value, false);
        jsonArray.getList().set(index, val);
    }

    static class KeyVernier {

        KeyVernier(String key) {
        }

        String currentKey() {
            return "";
        }

        int currentIndex() {
            return 0;
        }

        void next() {
        }

        boolean isCurrentTypeObject() {
            return false;
        }

        boolean isCurrentTypeArray() {
            return false;
        }

        boolean isNextTypeObject() {
            return false;
        }

        boolean isNextTypeArray() {
            return false;
        }

        boolean hasNext() {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static Object checkAndCopy(Object obj, boolean copy) {
        if (obj == null
                || obj instanceof Number && !(obj instanceof BigDecimal)
                || obj instanceof Boolean
                || obj instanceof String
                || obj instanceof Character) {
            return obj;
        } else if (obj instanceof CharSequence) {
            obj = obj.toString();
        } else if (obj instanceof Map && copy) {
            obj = (new JsonObject((Map) obj)).copy().getMap();
        } else if (obj instanceof List && copy) {
            obj = (new JsonArray((List) obj)).copy().getList();
        } else if (obj instanceof byte[]) {
            obj = Base64.getEncoder().encodeToString((byte[]) obj);
        } else if (obj instanceof Instant) {
            obj = DateTimeFormatter.ISO_INSTANT.format((Instant) obj);
        } else if (obj instanceof Date) {
            obj = DateTimeFormatter.ISO_INSTANT.format(((Date) obj).toInstant());
        } else {
            throw new IllegalStateException("Illegal type in JsonObject: " + obj.getClass());
        }
        return obj;
    }


    static Long getLong(Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof Long) {
            return (Long) number;
        } else {
            return number.longValue();
        }
    }

    static Double getDouble(Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof Double) {
            return (Double) number;
        } else {
            return number.doubleValue();
        }
    }

    static Float getFloat(Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof Float) {
            return (Float) number;
        } else {
            return number.floatValue();
        }
    }

    static Integer getInteger(Number number) {
        if (number == null) {
            return null;
        } else if (number instanceof Integer) {
            return (Integer) number;
        } else {
            return number.intValue();
        }
    }

    static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> T apply(Action<T> action) {
        try {
            return action.apply();
        } catch (Exception e) {
            throw new JojoException(e.getMessage(), e);
        }
    }

    interface Action<T> {

        T apply() throws Exception;
    }

    public static class JojoException extends RuntimeException {

        public JojoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class JsonObjectSerializer extends JsonSerializer<JsonObject> {
        @Override
        public void serialize(JsonObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.getMap());
        }
    }

    private static class JsonArraySerializer extends JsonSerializer<JsonArray> {
        @Override
        public void serialize(JsonArray value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.getList());
        }
    }

    private static class InstantSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(DateTimeFormatter.ISO_INSTANT.format(value));
        }
    }

    private static class DateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(DateTimeFormatter.ISO_INSTANT.format(value.toInstant()));
        }
    }

    private static class ByteArraySerializer extends JsonSerializer<byte[]> {
        private final Base64.Encoder BASE64 = Base64.getEncoder();

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(BASE64.encodeToString(value));
        }
    }


}
