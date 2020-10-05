package com.hyd.dao.mate.util;

import org.junit.Test;

import java.util.Collection;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class BatchTest {

    @Test
    public void testBatch() throws Exception {
        Batch.with(IntStream.of(1,2,3,4,5)).size(3).forEachBatch(System.out::println);

        int count = Batch.with(IntStream.of(1, 2, 3, 4, 5)).size(3).sumEachBatch(Collection::size).getResultCount();
        assertEquals(5, count);
    }
}
