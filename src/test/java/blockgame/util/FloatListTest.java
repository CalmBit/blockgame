package blockgame.util;

import blockgame.util.container.FloatList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FloatListTest {
    static final int CAPACITY = 32;
    FloatList list = new FloatList(CAPACITY);

    @Test
    @DisplayName("Initial capacity on initialization is correct")
    void initialCapacityIsCorrect() {
        Assertions.assertEquals(CAPACITY, list.getCapacity());
    }

    @Test
    @DisplayName("Initial length on initialization is zero")
    void initialLengthIsZero() {
        Assertions.assertEquals(0, list.getLength());
    }

    @Test
    @DisplayName("Single append adds one to length and maintains capacity")
    void singleAppendEffects() {
        list.append(1.0f);
        Assertions.assertEquals(1, list.getLength());
        Assertions.assertEquals(CAPACITY, list.getCapacity());
    }



}
