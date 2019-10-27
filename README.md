# Jojo

[![Release](https://jitpack.io/v/cbdyzj/jojo.svg)](https://jitpack.io/#cbdyzj/jojo)


- Using Jackson
- See Vert.x

## Sample

```java
public class JojoTest {

    @Test
    public void test0() {
        Human human = new Human();
        human.setAge(1);
        human.setBirthday(new Date(0));
        human.setName("Ada");
        human.setHobbies(Arrays.asList("a", "b", "c"));
        JsonObject jo1 = JsonObject.mapFrom(human);
        JsonObject jo2 = new JsonObject()
                .put("age", 1)
                .put("name", "Ada")
                .put("birthday", new Date(0))
                .put("hobbies", new JsonArray().add("a").add("b").add("c"));
        boolean equals = jo2.equals(jo1);
        Assertions.assertTrue(equals);
    }

    @Test
    public void test1() {
        Human human = new Human();
        human.setName("Ada");
        human.setAge(17);
        human.setBirthday(new Date(0));
        human.setHobbies(Arrays.asList("Painting", "Reading"));

        Box<Human> humanBox = new Box<>();
        humanBox.setLabel("human");
        humanBox.setData(human);

        List<Box<Human>> boxes = Collections.singletonList(humanBox);

        List humans = Json.decodeValue(Json.encode(boxes), new TypeReference<List<Box<Human>>>() { });
        Assertions.assertEquals(1, humans.size());
    }
}
```
