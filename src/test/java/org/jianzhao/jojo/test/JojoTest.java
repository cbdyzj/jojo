package org.jianzhao.jojo.test;

import org.jianzhao.jojo.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class JojoTest {

    @Test
    public void test() {
        Date date = new Date();
        User ada = new User("Ada", 17, date);
        assert new JsonObject()
                .put("name", "Ada")
                .put("age", 17)
                .put("birthday", date)
                .mapTo(User.class)
                .equals(ada);
    }
}
