package org.jianzhao.jojo;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class JsonObject implements Iterable<Map.Entry<String, Object>> {

    private Map<String, Object> map;

    public JsonObject(String json) {
        fromJson(json);
    }

    public JsonObject() {
        map = new LinkedHashMap<>();
    }

    public JsonObject(Map<String, Object> map) {
        this.map = map;
    }

    public JsonObject(ByteBuffer buf) {
        fromBuffer(buf);
    }

    @SuppressWarnings("unchecked")
    public static JsonObject mapFrom(Object obj) {
        return new JsonObject((Map<String, Object>) Json.mapper.convertValue(obj, Map.class));
    }

    public <T> T mapTo(Class<T> type) {
        return Json.mapper.convertValue(map, type);
    }

    public String getString(String key) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs == null ? null : cs.toString();
    }

    public Integer getInteger(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        return Json.getInteger(number);
    }

    public Long getLong(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        return Json.getLong(number);
    }

    public Double getDouble(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        return Json.getDouble(number);
    }

    public Float getFloat(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        return Json.getFloat(number);
    }

    public Boolean getBoolean(String key) {
        Objects.requireNonNull(key);
        return (Boolean) map.get(key);
    }

    @SuppressWarnings("unchecked")
    public JsonObject getJsonObject(String key) {
        Objects.requireNonNull(key);
        Map val = (Map) this.map.get(key);
        if (val == null) {
            return null;
        } else {
            return new JsonObject(val);
        }
    }

    public JsonArray getJsonArray(String key) {
        Objects.requireNonNull(key);
        List val = (List) map.get(key);
        if (val == null) {
            return null;
        } else {
            return new JsonArray(val);
        }
    }

    public byte[] getBinary(String key) {
        Objects.requireNonNull(key);
        String encoded = (String) map.get(key);
        return encoded == null ? null : Base64.getDecoder().decode(encoded);
    }

    public Instant getInstant(String key) {
        Objects.requireNonNull(key);
        String encoded = (String) map.get(key);
        return encoded == null ? null : Instant.from(DateTimeFormatter.ISO_INSTANT.parse(encoded));
    }

    public Date getDate(String key) {
        Objects.requireNonNull(key);
        String encoded = (String) map.get(key);
        return encoded == null ? null : Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(encoded)));
    }

    @SuppressWarnings("unchecked")
    public Object getValue(String key) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        if (val instanceof Map) {
            val = new JsonObject((Map) val);
        } else if (val instanceof List) {
            val = new JsonArray((List) val);
        }
        return val;
    }

    public String getString(String key, String def) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs != null || map.containsKey(key) ? cs == null ? null : cs.toString() : def;
    }

    public Integer getInteger(String key, Integer def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        Integer iVal = Json.getInteger(val);
        return iVal != null ? iVal : def;
    }

    public Long getLong(String key, Long def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        Long lVal = Json.getLong(val);
        return lVal != null ? lVal : def;
    }

    public Double getDouble(String key, Double def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        Double dVal = Json.getDouble(val);
        return dVal != null ? dVal : def;
    }

    public Float getFloat(String key, Float def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        Float fVal = Json.getFloat(val);
        return fVal != null ? fVal : def;
    }

    public Boolean getBoolean(String key, Boolean def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ? (Boolean) val : def;
    }

    public JsonObject getJsonObject(String key, JsonObject def) {
        JsonObject val = getJsonObject(key);
        return val != null || map.containsKey(key) ? val : def;
    }

    public JsonArray getJsonArray(String key, JsonArray def) {
        JsonArray val = getJsonArray(key);
        return val != null || map.containsKey(key) ? val : def;
    }

    public byte[] getBinary(String key, byte[] def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ? (val == null ? null : Base64.getDecoder().decode((String) val)) : def;
    }

    public Instant getInstant(String key, Instant def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ?
                (val == null ? null : Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String) val))) : def;
    }

    public Date getDate(String key, Date def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ?
                (val == null ? null : Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String) val)))) : def;
    }

    public Object getValue(String key, Object def) {
        Objects.requireNonNull(key);
        Object val = getValue(key);
        return val != null || map.containsKey(key) ? val : def;
    }

    public boolean containsKey(String key) {
        Objects.requireNonNull(key);
        return map.containsKey(key);
    }

    public Set<String> fieldNames() {
        return map.keySet();
    }

    public JsonObject put(String key, Enum value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : value.name());
        return this;
    }

    public JsonObject put(String key, CharSequence value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : value.toString());
        return this;
    }

    public JsonObject put(String key, String value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject put(String key, Integer value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject put(String key, Long value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject put(String key, Double value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject put(String key, Float value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject put(String key, Boolean value) {
        Objects.requireNonNull(key);
        map.put(key, value);
        return this;
    }

    public JsonObject putNull(String key) {
        Objects.requireNonNull(key);
        map.put(key, null);
        return this;
    }

    public JsonObject put(String key, JsonObject value) {
        Objects.requireNonNull(key);
        map.put(key, value.getMap());
        return this;
    }

    public JsonObject put(String key, JsonArray value) {
        Objects.requireNonNull(key);
        map.put(key, value.getList());
        return this;
    }

    public JsonObject put(String key, byte[] value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : Base64.getEncoder().encodeToString(value));
        return this;
    }

    public JsonObject put(String key, Instant value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : DateTimeFormatter.ISO_INSTANT.format(value));
        return this;
    }

    public JsonObject put(String key, Date value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : DateTimeFormatter.ISO_INSTANT.format(value.toInstant()));
        return this;
    }

    public JsonObject put(String key, Object value) {
        Objects.requireNonNull(key);
        value = Json.checkAndCopy(value, false);
        map.put(key, value);
        return this;
    }

    public Object remove(String key) {
        return map.remove(key);
    }

    public JsonObject mergeIn(JsonObject other) {
        return mergeIn(other, false);
    }

    public JsonObject mergeIn(JsonObject other, boolean deep) {
        return mergeIn(other, deep ? Integer.MAX_VALUE : 1);
    }

    @SuppressWarnings("unchecked")
    public JsonObject mergeIn(JsonObject other, int depth) {
        if (depth < 1) {
            return this;
        }
        if (depth == 1) {
            map.putAll(other.map);
            return this;
        }
        for (Map.Entry<String, Object> e : other.map.entrySet()) {
            if (e.getValue() == null) {
                map.put(e.getKey(), null);
            } else {
                map.merge(e.getKey(), e.getValue(), (oldVal, newVal) -> {
                    if (oldVal instanceof Map) {
                        oldVal = new JsonObject((Map) oldVal);
                    }
                    if (newVal instanceof Map) {
                        newVal = new JsonObject((Map) newVal);
                    }
                    if (oldVal instanceof JsonObject && newVal instanceof JsonObject) {
                        return ((JsonObject) oldVal).mergeIn((JsonObject) newVal, depth - 1);
                    }
                    return newVal;
                });
            }
        }
        return this;
    }

    public String encode() {
        return Json.encode(map);
    }


    public ByteBuffer toBuffer() {
        return Json.encodeToBuffer(map);
    }

    public JsonObject copy() {
        Map<String, Object> copiedMap;
        if (map instanceof LinkedHashMap) {
            copiedMap = new LinkedHashMap<>(map.size());
        } else {
            copiedMap = new HashMap<>(map.size());
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object val = entry.getValue();
            val = Json.checkAndCopy(val, true);
            copiedMap.put(entry.getKey(), val);
        }
        return new JsonObject(copiedMap);
    }

    public Map<String, Object> getMap() {
        return map;
    }


    public Stream<Map.Entry<String, Object>> stream() {
        return Json.asStream(iterator());
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new JsonObjectIterator(map.entrySet().iterator());
    }

    public int size() {
        return map.size();
    }

    public JsonObject clear() {
        map.clear();
        return this;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        return encode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return objectEquals(map, o);
    }

    static boolean objectEquals(Map<?, ?> m1, Object o2) {
        Map<?, ?> m2;
        if (o2 instanceof JsonObject) {
            m2 = ((JsonObject) o2).map;
        } else if (o2 instanceof Map<?, ?>) {
            m2 = (Map<?, ?>) o2;
        } else {
            return false;
        }
        if (m1.size() != m2.size())
            return false;
        for (Map.Entry<?, ?> entry : m1.entrySet()) {
            Object val = entry.getValue();
            if (val == null) {
                if (m2.get(entry.getKey()) != null) {
                    return false;
                }
            } else {
                if (!equals(entry.getValue(), m2.get(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean equals(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 instanceof JsonObject) {
            return objectEquals(((JsonObject) o1).map, o2);
        }
        if (o1 instanceof Map<?, ?>) {
            return objectEquals((Map<?, ?>) o1, o2);
        }
        if (o1 instanceof JsonArray) {
            return JsonArray.arrayEquals(((JsonArray) o1).getList(), o2);
        }
        if (o1 instanceof List<?>) {
            return JsonArray.arrayEquals((List<?>) o1, o2);
        }
        if (o1 instanceof Number && o2 instanceof Number && o1.getClass() != o2.getClass()) {
            Number n1 = (Number) o1;
            Number n2 = (Number) o2;
            if (o1 instanceof Float || o1 instanceof Double || o2 instanceof Float || o2 instanceof Double) {
                return n1.doubleValue() == n2.doubleValue();
            } else {
                return n1.longValue() == n2.longValue();
            }
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @SuppressWarnings("unchecked")
    private void fromJson(String json) {
        map = Json.decodeValue(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    private void fromBuffer(ByteBuffer buf) {
        map = Json.decodeValue(buf, Map.class);
    }

    private class JsonObjectIterator implements Iterator<Map.Entry<String, Object>> {

        final Iterator<Map.Entry<String, Object>> entryIterator;

        JsonObjectIterator(Iterator<Map.Entry<String, Object>> entryIterator) {
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map.Entry<String, Object> next() {
            Map.Entry<String, Object> entry = entryIterator.next();
            if (entry.getValue() instanceof Map) {
                return new Entry(entry.getKey(), new JsonObject((Map) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                return new Entry(entry.getKey(), new JsonArray((List) entry.getValue()));
            }
            return entry;
        }

        @Override
        public void remove() {
            entryIterator.remove();
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static final class Entry implements Map.Entry<String, Object> {
        final String key;
        final Object value;

        public Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
