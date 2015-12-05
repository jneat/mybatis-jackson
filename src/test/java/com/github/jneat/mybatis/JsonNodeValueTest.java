package com.github.jneat.mybatis;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class JsonNodeValueTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"{}", true, false},
            {"[]", true, false},
            {"null", true, false},
            {"", true, true},
            {null, true, true},
            {"1", false, false},
            {"{test:1}", false, false},
            {"[1,2,3]", false, false}
        });
    }

    private String input;

    private boolean empty;

    private boolean missing;

    public JsonNodeValueTest(String input, boolean empty, boolean missing) {
        this.input = input;
        this.empty = empty;
        this.missing = missing;
    }

    @Test
    public void emptyOrNot() {
        JsonNodeValue value = JsonNodeValue.from(input);
        assertThat(value.isEmpty()).isEqualTo(empty);
        assertThat(value.get().isMissingNode()).isEqualTo(missing);
    }
}
