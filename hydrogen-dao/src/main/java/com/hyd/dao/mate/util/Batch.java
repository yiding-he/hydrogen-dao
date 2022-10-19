package com.hyd.dao.mate.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

/**
 * 将流转为批处理
 */
public class Batch<T> {

    public static final int DEFAULT_BATCH_SIZE = 100;

    public static <T> Batch<T> with(Stream<T> stream) {
        return new Batch<>(stream);
    }

    public static <T, S extends BaseStream<T, S>> Batch<T> with(BaseStream<T, S> stream) {
        return new Batch<>(stream);
    }

    public static <T> Batch<T> with(Collection<T> collection) {
        return new Batch<>(collection.stream());
    }

    //////////////////////////////////////////////////////////////

    private final BaseStream<T, ?> stream;

    private int batchSize = DEFAULT_BATCH_SIZE;

    private int resultCount = 0;

    public Batch(BaseStream<T, ?> stream) {
        this.stream = stream;
    }

    public Batch<T> size(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public void forEachBatch(Consumer<Collection<T>> consumer) {
        List<T> buffer = new ArrayList<>();

        var iterator = stream.iterator();
        while (iterator.hasNext()) {
            var t = iterator.next();
            buffer.add(t);
            if (buffer.size() >= batchSize) {
                consumer.accept(new ArrayList<>(buffer));
                buffer.clear();
            }
        }

        if (buffer.size() > 0) {
            consumer.accept(buffer);
        }
    }

    public Batch<T> sumEachBatch(Function<Collection<T>, Integer> func) {
        List<T> list = new ArrayList<>();

        var iterator = stream.iterator();
        while (iterator.hasNext()) {
            var t = iterator.next();
            list.add(t);
            if (list.size() >= batchSize) {
                resultCount += func.apply(new ArrayList<>(list));
                list.clear();
            }
        }

        if (list.size() > 0) {
            resultCount += func.apply(list);
        }

        return this;
    }

    public int getResultCount() {
        return resultCount;
    }
}
