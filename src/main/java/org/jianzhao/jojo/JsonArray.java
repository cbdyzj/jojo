package org.jianzhao.jojo;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class JsonArray implements Iterable<Object> {

    private List<Object> list;

    public JsonArray(String json) {
        fromJson(json);
    }

    public JsonArray() {
        list = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public JsonArray(List list) {
        this.list = list;
    }

    public JsonArray(ByteBuffer buf) {
        fromBuffer(buf);
    }

    public String getString(int pos) {
        CharSequence cs = (CharSequence) list.get(pos);
        return cs == null ? null : cs.toString();
    }

    public Integer getInteger(int pos) {
        Number number = (Number) list.get(pos);
        return Json.getInteger(number);
    }

    public Long getLong(int pos) {
        Number number = (Number) list.get(pos);
        return Json.getLong(number);
    }

    public Double getDouble(int pos) {
        Number number = (Number) list.get(pos);
        return Json.getDouble(number);
    }

    public Float getFloat(int pos) {
        Number number = (Number) list.get(pos);
        return Json.getFloat(number);
    }

    public Boolean getBoolean(int pos) {
        return (Boolean) list.get(pos);
    }

    @SuppressWarnings("unchecked")
    public JsonObject getJsonObject(int pos) {
        Map val = (Map) list.get(pos);
        if (val == null) {
            return null;
        } else {
            return new JsonObject(val);
        }
    }

    public JsonArray getJsonArray(int pos) {
        List val = (List) this.list.get(pos);
        if (val == null) {
            return null;
        } else {
            return new JsonArray(val);
        }
    }

    public byte[] getBinary(int pos) {
        String val = (String) list.get(pos);
        if (val == null) {
            return null;
        } else {
            return Base64.getDecoder().decode(val);
        }
    }

    public Instant getInstant(int pos) {
        String val = (String) list.get(pos);
        if (val == null) {
            return null;
        } else {
            return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(val));
        }
    }

    public Date getDate(int pos) {
        String val = (String) list.get(pos);
        if (val == null) {
            return null;
        } else {
            Instant ins = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(val));
            return Date.from(ins);
        }
    }

    public Object getValue(int pos) {
        return list.get(pos);
    }

    public boolean hasNull(int pos) {
        return list.get(pos) == null;
    }

    public JsonArray add(Enum value) {
        Objects.requireNonNull(value);
        list.add(value.name());
        return this;
    }

    public JsonArray add(CharSequence value) {
        Objects.requireNonNull(value);
        list.add(value.toString());
        return this;
    }

    public JsonArray add(String value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Integer value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Long value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Double value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Float value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray add(Boolean value) {
        Objects.requireNonNull(value);
        list.add(value);
        return this;
    }

    public JsonArray addNull() {
        list.add(null);
        return this;
    }

    public JsonArray add(JsonObject value) {
        Objects.requireNonNull(value);
        list.add(value.getMap());
        return this;
    }

    public JsonArray add(JsonArray value) {
        Objects.requireNonNull(value);
        list.add(value.getList());
        return this;
    }

    public JsonArray add(byte[] value) {
        Objects.requireNonNull(value);
        list.add(Base64.getEncoder().encodeToString(value));
        return this;
    }

    public JsonArray add(Instant value) {
        Objects.requireNonNull(value);
        list.add(DateTimeFormatter.ISO_INSTANT.format(value));
        return this;
    }

    public JsonArray add(Date value) {
        Objects.requireNonNull(value);
        list.add(DateTimeFormatter.ISO_INSTANT.format(value.toInstant()));
        return this;
    }

    public JsonArray add(Object value) {
        Objects.requireNonNull(value);
        value = Json.checkAndCopy(value, false);
        list.add(value);
        return this;
    }

    public JsonArray addAll(JsonArray array) {
        Objects.requireNonNull(array);
        list.addAll(array.list);
        return this;
    }

    public boolean contains(Object value) {
        return list.contains(value);
    }

    @SuppressWarnings("unchecked")
    public Object remove(int pos) {
        Object removed = list.remove(pos);
        if (removed instanceof Map) {
            return new JsonObject((Map) removed);
        } else if (removed instanceof ArrayList) {
            return new JsonArray((List) removed);
        }
        return removed;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List getList() {
        return this.list;
    }

    public JsonArray clear() {
        list.clear();
        return this;
    }

    @Override
    public Iterator<Object> iterator() {
        return new JsonArrayIterator(list.iterator());
    }

    public String encode() {
        return Json.encode(this.list);
    }

    public ByteBuffer toBuffer() {
        return Json.encodeToBuffer(list);
    }

    public JsonArray copy() {
        List<Object> copiedList = new ArrayList<>(list.size());
        for (Object val : list) {
            val = Json.checkAndCopy(val, true);
            copiedList.add(val);
        }
        return new JsonArray(copiedList);
    }

    public Stream<Object> stream() {
        return Json.asStream(iterator());
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
        return arrayEquals(list, o);
    }

    static boolean arrayEquals(List<?> l1, Object o2) {
        List<?> l2;
        if (o2 instanceof JsonArray) {
            l2 = ((JsonArray) o2).list;
        } else if (o2 instanceof List<?>) {
            l2 = (List<?>) o2;
        } else {
            return false;
        }
        if (l1.size() != l2.size())
            return false;
        Iterator<?> iter = l2.iterator();
        for (Object entry : l1) {
            Object other = iter.next();
            if (entry == null) {
                if (other != null) {
                    return false;
                }
            } else if (!JsonObject.equals(entry, other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @SuppressWarnings("unchecked")
    private void fromJson(String json) {
        list = Json.decodeValue(json, List.class);
    }

    @SuppressWarnings("unchecked")
    private void fromBuffer(ByteBuffer buf) {
        list = Json.decodeValue(buf, List.class);
    }

    private static class JsonArrayIterator implements Iterator<Object> {

        final Iterator<Object> listIterator;

        JsonArrayIterator(Iterator<Object> listIterator) {
            this.listIterator = listIterator;
        }

        @Override
        public boolean hasNext() {
            return listIterator.hasNext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object next() {
            Object val = listIterator.next();
            if (val instanceof Map) {
                val = new JsonObject((Map) val);
            } else if (val instanceof List) {
                val = new JsonArray((List) val);
            }
            return val;
        }

        @Override
        public void remove() {
            listIterator.remove();
        }
    }


}
