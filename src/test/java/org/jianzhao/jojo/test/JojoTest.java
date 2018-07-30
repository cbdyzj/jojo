package org.jianzhao.jojo.test;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jianzhao.jojo.Json;
import org.jianzhao.jojo.JsonArray;
import org.jianzhao.jojo.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class JojoTest {

    @Test
    public void test() {
        Human human = new Human();
        human.setAge(1);
        human.setBirthday(new Date(0));
        human.setName("Ada");
        human.setHobbies(Stream.of("a", "b", "c").collect(Collectors.toList()));
        JsonObject entries = JsonObject.mapFrom(human);
        JsonObject put = new JsonObject()
                .put("age", 1)
                .put("name", "Ada")
                .put("birthday", new Date(0))
                .put("hobbies", new JsonArray().add("a").add("b").add("c"));
        boolean equals = put.equals(entries);
        Assertions.assertTrue(equals);
    }

    @Test
    public void typeTest() {
        Human human = new Human();
        human.setName("Ada");
        human.setAge(17);
        human.setBirthday(new Date(0));
        human.setHobbies(Arrays.asList("Painting", "Reading"));

        Box<Human> hb = new Box<>();
        hb.setLabel("human");
        hb.setData(human);

        List<Box<Human>> boxes = Collections.singletonList(hb);

        List<?> humans = Json.decodeValue(Json.encode(boxes), new TypeReference<List<Box<Human>>>() { });
        Assertions.assertEquals(1, humans.size());
    }
}


