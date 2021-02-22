package com.hyd.dao.mate.util;

import org.junit.Test;

import java.util.stream.IntStream;

public class BatchPipelineTest {

    @Test
    public void testBatch() throws Exception {

        BatchPipeline<Integer> bp = new BatchPipeline<Integer>()
            .setBatchSize(8)
            .setBatchOperation(System.out::println);

        bp.feed(1, 2, 3, 4, 5, 5, 6, 7, 8, 8, 6, 4, 4, 56, 6, 7, 7, 4);
        bp.feed(IntStream.of(6, 5, 4, 5, 6, 7, 8, 89, 9, 90, 8, 7, 6, 5, 4, 3, 22));
        bp.flush();
    }
}
