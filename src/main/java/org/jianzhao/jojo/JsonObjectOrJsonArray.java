package org.jianzhao.jojo;

public class JsonObjectOrJsonArray<T> {

    private T value;

    private JsonObjectOrJsonArray(T value) {
        if (value instanceof JsonObject || value instanceof JsonArray) {
            this.value = value;
        } else {
            throw new Json.JojoException("JsonObject or JsonArray instance required", null);
        }
    }

    public static <T> JsonObjectOrJsonArray<T> of(T value) {
        return new JsonObjectOrJsonArray<>(value);
    }

    public T get() {
        return this.value;
    }

    public boolean isJsonObject() {
        return this.value instanceof JsonObject;
    }

    public boolean isJsonArray() {
        return this.value instanceof JsonArray;
    }
}
