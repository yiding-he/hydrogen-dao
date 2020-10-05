package com.hyd.dao.mate.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;

public class Batch<T> {

    public static final int DEFAULT_BATCH_SIZE = 100;

    public static <T> Batch<T> with(Collection<T> collection) {
        return new Batch<>(collection.iterator());
    }

    public static <T, S extends BaseStream<T, S>> Batch<T> with(BaseStream<T, S> stream) {
        return new Batch<>(stream.iterator());
    }

    public static <T> Batch<T> with(Iterator<T> iterator) {
        return new Batch<>(iterator);
    }

    //////////////////////////////////////////////////////////////

    private final Iterator<T> iterator;

    private int batchSize = DEFAULT_BATCH_SIZE;

    private int resultCount = 0;

    public Batch(Iterator<T> iterator) {
        this.iterator = iterator;

    }

    public Batch<T> size(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public void forEachBatch(Consumer<Collection<T>> consumer) {
        List<T> list = new ArrayList<>();

        while (iterator.hasNext()) {
            T t = iterator.next();
            list.add(t);
            if (list.size() >= batchSize) {
                consumer.accept(new ArrayList<>(list));
                list.clear();
            }
        }

        if (list.size() > 0) {
            consumer.accept(list);
        }
    }

    public Batch<T> sumEachBatch(Function<Collection<T>, Integer> func) {
        List<T> list = new ArrayList<>();

        while (iterator.hasNext()) {
            T t = iterator.next();
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
