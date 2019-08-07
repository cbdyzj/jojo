package org.jianzhao.jojo.test;

import org.jianzhao.jojo.Json;
import org.jianzhao.jojo.JsonArray;
import org.jianzhao.jojo.JsonObject;
import org.jianzhao.jojo.JsonObjectOrJsonArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FlattenTest {

    @Test
    public void flatten() {
        JsonObject normal = new JsonObject()
                .put("foo", "foo")
                .put("bar", new JsonObject().put("b", "ar"))
                .put("baz", new JsonArray().add("b").add("a").add("z"));
        JsonObject flattened = Json.flatten(new JsonObjectOrJsonArray<>(normal));
        JsonObject expectedFlattened = new JsonObject()
                .put("foo", "foo")
                .put("bar.b", "ar")
                .put("baz[0]", "b")
                .put("baz[1]", "a")
                .put("baz[2]", "z");
        Assertions.assertEquals(flattened, expectedFlattened);
    }

    @Test
    public void unFlatten() {
        JsonObject flattened = new JsonObject()
                .put("foo", "foo")
                .put("bar.b", "ar")
                .put("baz[0]", "b")
                .put("baz[1]", "a")
                .put("baz[2]", "z");
        JsonObjectOrJsonArray<?> normal = Json.unFlatten(flattened);
        JsonObject expectedNormal = new JsonObject()
                .put("foo", "foo")
                .put("bar", new JsonObject().put("b", "ar"))
                .put("baz", new JsonArray().add("b").add("a").add("z"));
        Assertions.assertEquals(normal.get(), expectedNormal);
    }

    @Test
    public void flattenArray() {
        JsonObject flattened = Json.flatten(
                new JsonObjectOrJsonArray<>(
                        new JsonArray()
                                .add(new JsonArray()
                                        .add(new JsonArray()
                                                .add(new JsonArray()
                                                        .add("0"))))));
        JsonObject expectedFlattened = new JsonObject().put("[0][0][0][0]", "0");
        Assertions.assertEquals(flattened, expectedFlattened);
    }
}
