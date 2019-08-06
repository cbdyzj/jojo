package org.jianzhao.jojo.test;

import org.jianzhao.jojo.Json;
import org.jianzhao.jojo.JsonArray;
import org.jianzhao.jojo.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FlattenTest {

    @Test
    public void flatten() {
        JsonObject jsonObject = new JsonObject()
                .put("foo", "foo")
                .put("bar", "bar")
                .put("baz", new JsonArray().add("b").add("a").add("z"));
        JsonObject flattened = Json.flatten(jsonObject);
        String actual = "{\"foo\":\"foo\",\"bar\":\"bar\",\"baz[0]\":\"b\",\"baz[1]\":\"a\",\"baz[2]\":\"z\"}";
        Assertions.assertEquals(flattened.encode(), actual);
    }
}
